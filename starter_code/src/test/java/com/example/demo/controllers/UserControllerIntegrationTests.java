package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.example.demo.utils.UserTestUtils;
import com.example.demo.utils.LoginUserRequest;
import com.example.demo.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class UserControllerIntegrationTests {

	private static final String TEST_USERNAME = "testusername";
	private static final String TEST_PASSWORD = "testpassword";
	private static final String TEST_INVALID_SUFFIX = "INVALID";
	private static final String TEST_INVALID_PASSWORD = "badpwd";
	private static final String TEST_UNMATCHED_PASSWORD = "testunmatchedpwd";
	private static final String HOST_URL = TestUtils.HOST_URL;
	private static final String FIND_USER_BY_ID_ENDPOINT = UserController.FIND_USER_BY_ID_ENDPOINT;
	private static final String FIND_USER_BY_USERNAME_ENDPOINT = UserController.FIND_USER_BY_USERNAME_ENDPOINT;
	private static final String TEST_INVALID_USERNAME = "testInvalidUsername";
	@Autowired
	private UserController userController;
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;
	private UserTestUtils userTestUtils;

	@BeforeEach
	public void beforeEach() {
		userTestUtils = new UserTestUtils(restTemplate, port);
	}

	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}

	@Test
	public void canCreateUser() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		ResponseEntity<User> response = userTestUtils.createUser(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(request.getUsername(), getUser().getUsername());
	}

	@Test
	public void canHandleCreateUserWithInvalidPassword() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest(
				TEST_USERNAME, TEST_INVALID_PASSWORD, TEST_INVALID_PASSWORD);
		ResponseEntity<User> response = userTestUtils.createUser(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void canHandleCreateUserWithUnmatchedPassword() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest(
				TEST_USERNAME, TEST_PASSWORD, TEST_UNMATCHED_PASSWORD);
		ResponseEntity<User> response = userTestUtils.createUser(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void canCreateUserAndLogin() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		ResponseEntity<String> response = userTestUtils.createAndLoginUser(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<String> authorizations = response.getHeaders().get("Authorization");
		assertNotNull(authorizations);
	}

	@Test
	public void canHandleLoginFromInvalidUser() throws JsonProcessingException {
		LoginUserRequest loginUserRequest = new LoginUserRequest(TEST_INVALID_USERNAME, TEST_INVALID_PASSWORD);

		ResponseEntity<String> response = userTestUtils.login(loginUserRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	@Test
	public void canFindUserById() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange(
				HOST_URL + port + FIND_USER_BY_ID_ENDPOINT + getUser().getId(), 
				HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(getUser().getId(), userResponse.getBody().getId());
		assertEquals(getUser().getUsername(), userResponse.getBody().getUsername());
	}

	@Test
	public void canHandleFindUserByInvalidId() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<String> userResponse = restTemplate.exchange(
				HOST_URL + port + FIND_USER_BY_ID_ENDPOINT + getUser().getId() + 1l, 
				HttpMethod.GET, getJwtEntity(),
				String.class);

		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
	}

	@Test
	public void canGetUserByUsername() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange(
				HOST_URL + port + FIND_USER_BY_USERNAME_ENDPOINT + getUser().getUsername(), 
				HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(getUser().getId(), userResponse.getBody().getId());
		assertEquals(getUser().getUsername(), userResponse.getBody().getUsername());
	}

	@Test
	public void canHandleGetUserByInvalidUserName() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange(
				HOST_URL + port + FIND_USER_BY_USERNAME_ENDPOINT + getUser().getUsername() + TEST_INVALID_SUFFIX, 
				HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
	}

	private void createAndAuthorizeUser() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		userTestUtils.createAndLoginUser(request);
	}

	private User getUser() {
		return userTestUtils.getUser();
	}

	private HttpEntity<String> getJwtEntity() {
		return userTestUtils.getJwtEntity();
	}

}

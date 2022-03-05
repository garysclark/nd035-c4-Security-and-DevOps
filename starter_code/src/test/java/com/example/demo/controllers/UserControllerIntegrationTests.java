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
import com.example.demo.utils.AuthorizedUser;
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
	@Autowired
	private UserController userController;
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;
	private AuthorizedUser authorizedUser;

	@BeforeEach
	public void beforeEach() {
		authorizedUser = new AuthorizedUser(restTemplate, port);
	}
	
	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}

	@Test
	public void canCreateUser() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		ResponseEntity<User> response = authorizedUser.create(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(request.getUsername(), getUser().getUsername());
	}
	
	@Test
	public void canHandleCreateUserWithInvalidPassword() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest(TEST_USERNAME, TEST_INVALID_PASSWORD, TEST_INVALID_PASSWORD);
		ResponseEntity<User> response = authorizedUser.create(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void canHandleCreateUserWithUnmatchedPassword() {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest(TEST_USERNAME, TEST_PASSWORD, TEST_UNMATCHED_PASSWORD);
		ResponseEntity<User> response = authorizedUser.create(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void canCreateUserAndLogin() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		ResponseEntity<String> response = authorizedUser.createAndLogin(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<String> authorizations = response.getHeaders().get("Authorization");
		assertNotNull(authorizations);
	}
	
	@Test
	public void canGetUserById() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/id/" + getUser().getId(), HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(getUser().getId(), userResponse.getBody().getId());
		assertEquals(getUser().getUsername(), userResponse.getBody().getUsername());
	}
	
	@Test
	public void canHandleGetUserByInvalidId() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<String> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/id/" + getUser().getId() + 1l, HttpMethod.GET, getJwtEntity(),
				String.class);

		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
	}
	
	@Test
	public void canGetUserByUsername() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/" + getUser().getUsername(), HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(getUser().getId(), userResponse.getBody().getId());
		assertEquals(getUser().getUsername(), userResponse.getBody().getUsername());
	}
	
	@Test
	public void canHandleGetUserByInvalidUserName() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/" + getUser().getUsername() + TEST_INVALID_SUFFIX, HttpMethod.GET, getJwtEntity(),
				User.class);

		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
	}

	private void createAndAuthorizeUser() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		authorizedUser.createAndLogin(request);
	}

	private User getUser() {
		return authorizedUser.getUser();
	}

	private HttpEntity<String> getJwtEntity() {
		return authorizedUser.getJwtEntity();
	}

}

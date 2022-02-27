package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class UserControllerIntegrationTests {

	public class LoginUserRequest {

		private String username;
		private String password;

		public LoginUserRequest(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

	}

	private static final String TEST_USERNAME = "testusername";
	private static final String TEST_PASSWORD = "testpassword";
	@Autowired
	private UserController userController;
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;
	private User testUser;
	private HttpEntity<String> testJwtEntity;

	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}

	@Test
	public void canCreateUser() {
		ResponseEntity<User> response = createUser();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = response.getBody();
		assertEquals(TEST_USERNAME, user.getUsername());
	}

	@Test
	public void canCreateUserAndLogin() throws JsonProcessingException {
		createUser();

		ResponseEntity<String> loginResponse = loginUser();

		assertNotNull(loginResponse);
		assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		List<String> authorizations = loginResponse.getHeaders().get("Authorization");
		assertNotNull(authorizations);
	}
	
	@Test
	public void canGetUserById() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/id/" + testUser.getId(), HttpMethod.GET, testJwtEntity,
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(testUser.getId(), userResponse.getBody().getId());
		assertEquals(testUser.getUsername(), userResponse.getBody().getUsername());
	}
	
	@Test
	public void canHandleGetUserByInvalidId() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<String> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/id/" + testUser.getId() + 1l, HttpMethod.GET, testJwtEntity,
				String.class);

		assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
	}
	
	@Test
	public void canGetUserByUsername() throws JsonProcessingException {
		createAndAuthorizeUser();

		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/" + testUser.getUsername(), HttpMethod.GET, testJwtEntity,
				User.class);

		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(testUser.getId(), userResponse.getBody().getId());
		assertEquals(testUser.getUsername(), userResponse.getBody().getUsername());
	}

	private void createAndAuthorizeUser() throws JsonProcessingException {
		ResponseEntity<User> createResponse = createUser();
		testUser = createResponse.getBody();

		ResponseEntity<String> loginResponse = loginUser();

		List<String> authorizations = loginResponse.getHeaders().get("Authorization");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorizations.get(0));

		testJwtEntity = new HttpEntity<String>(headers);
	}

	private ResponseEntity<String> loginUser() throws JsonProcessingException {
		LoginUserRequest request = new LoginUserRequest(TEST_USERNAME, TEST_PASSWORD);
		String authenticationBody = getBody(request);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/login", authenticationBody, String.class);
		return response;
	}

	private ResponseEntity<User> createUser() {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(TEST_USERNAME);
		request.setPassword(TEST_PASSWORD);
		request.setConfirmPassword(TEST_PASSWORD);

		ResponseEntity<User> response = restTemplate.postForEntity("http://localhost:" + port + "/api/user/create", request, User.class);
		return response;
	}

	private String getBody(final LoginUserRequest loginUser) throws JsonProcessingException{
		return new ObjectMapper().writeValueAsString(loginUser);
	}

}

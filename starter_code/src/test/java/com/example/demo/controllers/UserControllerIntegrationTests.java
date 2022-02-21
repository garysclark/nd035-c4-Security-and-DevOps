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
import org.springframework.http.MediaType;
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

	public class LoginUser {

		private String username;
		private String password;

		public LoginUser(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
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

	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}
	
	@Test
	public void canCreateUser() {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(TEST_USERNAME);
		request.setPassword(TEST_PASSWORD);
		request.setConfirmPassword(TEST_PASSWORD);
		
		ResponseEntity<User> response = userController.createUser(request);
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = response.getBody();
		assertEquals(TEST_USERNAME, user.getUsername());
	}
	
	@Test
	public void canCreateUserAndLogin() throws JsonProcessingException {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(TEST_USERNAME);
		request.setPassword(TEST_PASSWORD);
		request.setConfirmPassword(TEST_PASSWORD);
		
		ResponseEntity<User> response = restTemplate.postForEntity("http://localhost:" + port + "/api/user/create", request, User.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = response.getBody();
		assertEquals(TEST_USERNAME, user.getUsername());
		
		LoginUser loginUser = new LoginUser(TEST_USERNAME, TEST_PASSWORD);
		String authenticationBody = getBody(loginUser);
		HttpHeaders authenticationHeaders = getHeaders();
		HttpEntity<String> authenticationEntity = new HttpEntity<String>(authenticationBody,
				authenticationHeaders);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/login", authenticationEntity, String.class);
		
		assertNotNull(loginResponse);
		assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		List<String> authorizations = loginResponse.getHeaders().get("Authorization");
		assertNotNull(authorizations);

		HttpHeaders headers = getHeaders();
		headers.set("Authorization", authorizations.get(0));
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		// Use Token to get Response
		ResponseEntity<User> userResponse = restTemplate.exchange("http://localhost:" + port + "/api/user/id/" + user.getId(), HttpMethod.GET, jwtEntity,
				User.class);
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertEquals(user.getId(), userResponse.getBody().getId());
		assertEquals(user.getUsername(), userResponse.getBody().getUsername());
}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	private String getBody(final LoginUser loginUser) throws JsonProcessingException{
		return new ObjectMapper().writeValueAsString(loginUser);
	}

}

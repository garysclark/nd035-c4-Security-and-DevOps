package com.example.demo.utils;

import java.util.List;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserTestUtils {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String LOGIN_ENDPOINT = "/login";

	private static final String HOST_URL = TestUtils.HOST_URL;

	private static final String CREATE_USER_ENDPOINT = UserController.CREATE_USER_ENDPOINT;

	private User user;
	private HttpEntity<String> jwtEntity;
	private TestRestTemplate testRestTemplate;
	private int port;

	public UserTestUtils(TestRestTemplate testRestTemplate, int port) {
		this.testRestTemplate = testRestTemplate;
		this.port = port;
	}

	public User getUser() {
		return user;
	}

	public HttpEntity<String> getJwtEntity() {
		return jwtEntity;
	}

	public ResponseEntity<User> createUser(CreateUserRequest request) {
		ResponseEntity<User> response = testRestTemplate.postForEntity(
				HOST_URL + port + CREATE_USER_ENDPOINT, request, User.class);
		if(response.getStatusCode() == HttpStatus.OK) {
			user = response.getBody();
		}
		return response;
	}

	public ResponseEntity<String> login(LoginUserRequest request) 
			throws JsonProcessingException {
		String authenticationBody = new ObjectMapper().writeValueAsString(request);
		ResponseEntity<String> response = testRestTemplate.postForEntity(
				HOST_URL + port + LOGIN_ENDPOINT, authenticationBody, String.class);

		if(response.getStatusCode() == HttpStatus.OK) {
			List<String> authorizations = response.getHeaders().get(AUTHORIZATION_HEADER);
			HttpHeaders headers = new HttpHeaders();
			headers.set(AUTHORIZATION_HEADER, authorizations.get(0));
			jwtEntity = new HttpEntity<String>(headers);
		}
		
		return response;
	}

	public ResponseEntity<String> createAndLoginUser(CreateUserRequest request) 
			throws JsonProcessingException {
		if(createUser(request).getStatusCode() != HttpStatus.OK) {
			return null;
		}
		return login(new LoginUserRequest(request.getUsername(), request.getPassword()));
	}

}

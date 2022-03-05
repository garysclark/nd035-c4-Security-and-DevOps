package com.example.demo.utils;

import java.util.List;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorizedUser {

	private User user;
	private HttpEntity<String> jwtEntity;
	private TestRestTemplate testRestTemplate;
	private int port;

	public AuthorizedUser(TestRestTemplate testRestTemplate, int port) {
		this.testRestTemplate = testRestTemplate;
		this.port = port;
	}

	public User getUser() {
		return user;
	}

	public HttpEntity<String> getJwtEntity() {
		return jwtEntity;
	}

	public ResponseEntity<User> create(CreateUserRequest request) {
		ResponseEntity<User> response = testRestTemplate.postForEntity("http://localhost:" + port + "/api/user/create", request, User.class);
		if(response.getStatusCode() == HttpStatus.OK) {
			user = response.getBody();
		}
		return response;
	}

	public ResponseEntity<String> login(LoginUserRequest request) throws JsonProcessingException {
		String authenticationBody = new ObjectMapper().writeValueAsString(request);
		ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/login", authenticationBody, String.class);

		if(response.getStatusCode() == HttpStatus.OK) {
			List<String> authorizations = response.getHeaders().get("Authorization");
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", authorizations.get(0));
			jwtEntity = new HttpEntity<String>(headers);
		}
		
		return response;
	}

	public ResponseEntity<String> createAndLogin(CreateUserRequest request) throws JsonProcessingException {
		if(create(request).getStatusCode() != HttpStatus.OK) {
			return null;
		}
		return login(new LoginUserRequest(request.getUsername(), request.getPassword()));
	}

}

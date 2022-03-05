package com.example.demo.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class AuthorizedUserTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int port;
	
	@Test
	public void canCreateAndLoginUserSequentially() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		AuthorizedUser authorizedUser = new AuthorizedUser(testRestTemplate, port);
		assertNotNull(authorizedUser);
		ResponseEntity<User> response = authorizedUser.create(request);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(request.getUsername(), authorizedUser.getUser().getUsername());
		assertNull(authorizedUser.getJwtEntity());
		
		LoginUserRequest loginUserRequest = new LoginUserRequest(request.getUsername(), request.getPassword());
		ResponseEntity<String> loginResponse = authorizedUser.login(loginUserRequest);
		assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		assertNotNull(authorizedUser.getJwtEntity());
	}
	
	@Test
	public void canCreateAndLoginUserAsASingleCall() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		AuthorizedUser authorizedUser = new AuthorizedUser(testRestTemplate, port);
		assertNotNull(authorizedUser);
		ResponseEntity<String> response = authorizedUser.createAndLogin(request);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(request.getUsername(), authorizedUser.getUser().getUsername());
		assertNotNull(authorizedUser.getJwtEntity());
	}
	
	@Disabled
	@Test
	public void canHandleInvalidLogin() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		AuthorizedUser authorizedUser = new AuthorizedUser(testRestTemplate, port);

		LoginUserRequest loginUserRequest = new LoginUserRequest(request.getUsername(), request.getPassword());
		ResponseEntity<String> loginResponse = authorizedUser.login(loginUserRequest);
		assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		assertNotNull(authorizedUser.getJwtEntity());

	}
}

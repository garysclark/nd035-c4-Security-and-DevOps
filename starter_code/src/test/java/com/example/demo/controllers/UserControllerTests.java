package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTests {

	private static final String TEST_USERNAME = "testUser";
	private static final String TEST_PASSWORD = "testpassword";
	private static final Long TEST_ID = 99l;

	@Mock
	private CartRepository mockCartRepository;
	
	@Mock
	private BCryptPasswordEncoder mockBCryptPasswordEncoder;
	
	@Mock
	private UserRepository mockUserRepository;

	@InjectMocks
	private UserController userController = new UserController();

	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.openMocks(this);
	}
	
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
		assertNotNull(user);
//		assertEquals(TEST_ID, user.getId());
	}
}

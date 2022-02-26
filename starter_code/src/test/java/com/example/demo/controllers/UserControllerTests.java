package com.example.demo.controllers;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.UserService;

public class UserControllerTests {

	private static final String TEST_USERNAME = "testUser";
	private static final String TEST_PASSWORD = "testpassword";
	private static final Long TEST_CART_ID = 1l;
	private static final String TEST_ENCRYPTED_PASSWORD = "encryptedpassword";
	private static final Long TEST_USER_ID = 1l;
	private static final String TEST_INVALID_PASSWORD = "badpwd";
	private static final String TEST_UNMATCHED_PASSWORD = "unmatchedpassword";
	private static final long TEST_INVALID_USER_ID = 99l;
	private static final String TEST_INVALID_USERNAME = "invalidUsername";
	
	@Mock
	private BCryptPasswordEncoder mockBCryptPasswordEncoder;

	@Mock
	private UserService mockUserService;
	
	@InjectMocks
	private UserController userController = new UserController();
	private AutoCloseable autoCloseable;

	@BeforeEach
	public void beforeEach() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		autoCloseable.close();
	}
	
	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}
	
	@Test
	public void canFindUserById() {
		User user = UserTests.getTestUser(TEST_USER_ID, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, getCart(TEST_CART_ID));
		when(mockUserService.findUserById(TEST_USER_ID)).thenReturn(user);

		ResponseEntity<User> response = userController.findById(TEST_USER_ID);
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
	}
	
	@Test
	public void canHandleFindByInvalidUserId() {
		when(mockUserService.findUserById(TEST_INVALID_USER_ID)).thenReturn(null);

		ResponseEntity<User> response = userController.findById(TEST_INVALID_USER_ID);
		
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void canFindUserByUsername() {
		User user = UserTests.getTestUser(TEST_USER_ID, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, getCart(TEST_CART_ID));
		when(mockUserService.findUserByUserName(TEST_USERNAME)).thenReturn(user);

		ResponseEntity<User> response = userController.findByUserName(TEST_USERNAME);
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
	}
	
	@Test
	public void canHandleFindByInvalidUserName() {
		when(mockUserService.findUserByUserName(TEST_INVALID_USERNAME)).thenReturn(null);

		ResponseEntity<User> response = userController.findByUserName(TEST_INVALID_USERNAME);
		
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	private Cart getCart(Long id) {
		Cart cart = new Cart();
		cart.setId(id);
		return cart;
	}

	@Test
	public void canCreateUser() {
		Cart savedCart = getCart(TEST_CART_ID);
		User user = UserTests.getTestUser(null, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, null);
		User savedUser = UserTests.getTestUser(TEST_USER_ID, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, savedCart);
		
		when(mockUserService.saveUser(user)).thenReturn(savedUser);
		when(mockBCryptPasswordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCRYPTED_PASSWORD);

		CreateUserRequest request = getRequest(TEST_USERNAME, TEST_PASSWORD, TEST_PASSWORD);
		
		ResponseEntity<User> response = userController.createUser(request);
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(savedUser, response.getBody());
	}
	
	@Test
	public void canHandleInvalidPassword() {
		CreateUserRequest request = getRequest(TEST_USERNAME, TEST_INVALID_PASSWORD, TEST_INVALID_PASSWORD);
		
		ResponseEntity<User> response = userController.createUser(request);
		
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void canHandleUnmatchedPasswords() {
		CreateUserRequest request = getRequest(TEST_USERNAME, TEST_PASSWORD, TEST_UNMATCHED_PASSWORD);
		
		ResponseEntity<User> response = userController.createUser(request);
		
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	private CreateUserRequest getRequest(String username, String password, String confirmPassword) {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setConfirmPassword(confirmPassword);
		return request;
	}
}

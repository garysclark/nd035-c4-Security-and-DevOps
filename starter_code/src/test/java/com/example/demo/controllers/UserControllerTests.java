package com.example.demo.controllers;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTests {

	private static final long TEST_SAVED_USER_ID = 1l;
	private static final String TEST_USERNAME = "testUser";
	private static final String TEST_PASSWORD = "testpassword";
	private static final Long TEST_ID = 99l;
	private static final String TEST_ENCRYPTED_PASSWORD = "encryptedpassword";
	private static final long TEST_NEW_USER_ID = 0l;

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
		Cart cart = new Cart();
		Cart savedCart = new Cart();
		savedCart.setId(TEST_ID);
		User user = new User(TEST_NEW_USER_ID, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, savedCart);
		User savedUser = new User(TEST_NEW_USER_ID, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, savedCart);
		assertEquals(user, savedUser);
		
		when(mockCartRepository.save(cart)).thenReturn(savedCart);
		when(mockUserRepository.save(user)).thenReturn(savedUser);
		when(mockBCryptPasswordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCRYPTED_PASSWORD);

		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(TEST_USERNAME);
		request.setPassword(TEST_PASSWORD);
		request.setConfirmPassword(TEST_PASSWORD);
		
		ResponseEntity<User> response = userController.createUser(request);
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User responseUser = response.getBody();
		assertNotNull(responseUser);
		assertEquals(savedUser.getId(), responseUser.getId());
	}
}

package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class UserServiceTests {

	private static final Long TEST_CART_ID = 1l;
	private static final String TEST_USERNAME = "testusername";
	private static final String TEST_PASSWORD = "password";
	private static final Long TEST_USER_ID = 1l;
	private static final Long TEST_INVALID_ID = 99l;
	@Mock
	private CartRepository mockCartRepository;
	@Mock
	private UserRepository mockUserRepository;
	
	@InjectMocks
	private UserService userService = new UserService();

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
	public void canAccessService() {
		assertNotNull(userService);
	}
	
	@Test
	public void canCreateUser() {
		Cart newCart = createCart(null);
		Cart repoCart = createCart(TEST_CART_ID);
		User newUser = createUser(null, TEST_USERNAME, TEST_PASSWORD, null);
		User repoUser = createUser(TEST_USER_ID, TEST_USERNAME, TEST_PASSWORD, newCart);
		when(mockCartRepository.save(newCart)).thenReturn(repoCart);
		when(mockUserRepository.save(newUser)).thenReturn(repoUser);
		
		User savedUser = userService.saveUser(newUser);
		
		assertNotNull(savedUser);
		assertEquals(repoUser, savedUser);
	}
	
	@Test
	public void canFindUserByUsername() {
		User repoUser = createUser(TEST_USER_ID, TEST_USERNAME, TEST_PASSWORD, createCart(TEST_CART_ID));
		when(mockUserRepository.findByUsername(TEST_USERNAME)).thenReturn(repoUser);
		
		User foundUser = userService.findUserByUserName(TEST_USERNAME);
		
		assertNotNull(foundUser);
		assertEquals(repoUser, foundUser);
	}
	
	@Test
	public void candFindUserById() {
		User repoUser = createUser(TEST_USER_ID, TEST_USERNAME, TEST_PASSWORD, createCart(TEST_CART_ID));
		when(mockUserRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(repoUser));
		
		User foundUser = userService.findUserById(TEST_USER_ID);
		
		assertNotNull(foundUser);
		assertEquals(repoUser, foundUser);
	}
	
	@Test
	public void canHandleUserNotFoundById() {
		when(mockUserRepository.findById(TEST_INVALID_ID)).thenReturn(Optional.ofNullable(null));

		assertThrows(EntityNotFoundException.class, ()->{userService.findUserById(TEST_INVALID_ID);});
	}

	private User createUser(Long id, String username, String password, Cart cart) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setId(id);
		user.setCart(cart);
		return user;
	}
	
	private Cart createCart(Long id) {
		Cart cart = new Cart();
		cart.setId(id);
		return cart;
	}
}

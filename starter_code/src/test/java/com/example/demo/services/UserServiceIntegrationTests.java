package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.model.persistence.repositories.CartRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class UserServiceIntegrationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private CartRepository cartRepository;

	@Test
	public void canAccessComponents() {
		assertNotNull(userService);
		assertNotNull(cartRepository);
	}
	
	@Test
	public void canCreateUser() {
		User user = UserTests.getTestUser();
		User savedUser = userService.saveUser(user);
		assertNotNull(savedUser);
		assertEquals(user.getUsername(), savedUser.getUsername());
		assertEquals(user.getPassword(), savedUser.getPassword());
		Cart cart = user.getCart();
		assertNotNull(cart);
		Optional<Cart> optionalCart = cartRepository.findById(cart.getId());
		assertTrue(optionalCart.isPresent());
	}
	
	@Test
	public void canValidateUserCartReferences() {
		User user = UserTests.getTestUser();
		User savedUser = userService.saveUser(user);
		User foundUser = userService.findUserById(savedUser.getId());
		assertEquals(foundUser, foundUser.getCart().getUser());
		User foundUser2 = userService.findUserById(savedUser.getId());
		assertEquals(foundUser, foundUser2);
	}
}

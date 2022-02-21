package com.example.demo.model.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserTests {

	private static final long TEST_ID = 99l;
	private static final String TEST_PASSWORD = "testpassword";
	private static final String TEST_USERNAME = "testusername";
	private static final Cart TEST_CART = CartTests.getTestCart();

	@Test
	public void canCreateUser() {
		User user = new User();
		assertNotNull(user);
	}
	
	@Test
	public void canSetGetAttributes() {
		User user = new User();
		user.setId(TEST_ID);
		user.setPassword(TEST_PASSWORD);
		user.setUsername(TEST_USERNAME);
		user.setCart(TEST_CART);
		validateAttributes(user);
	}
	
	@Test
	public void canCreateWithAttributes() {
		User user = new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_CART);
		validateAttributes(user);
	}
	
	@Test
	public void canValidateEquals() {
		assertEquals(getTestUser(), getTestUser());
	}

	public static User getTestUser() {
		return new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_CART);
	}

	private void validateAttributes(User user) {
		assertEquals(TEST_ID, user.getId());
		assertEquals(TEST_PASSWORD, user.getPassword());
		assertEquals(TEST_USERNAME, user.getUsername());
		assertEquals(TEST_CART, user.getCart());
	}
}

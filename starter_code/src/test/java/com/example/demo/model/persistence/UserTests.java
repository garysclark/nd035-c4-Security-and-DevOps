package com.example.demo.model.persistence;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class UserTests {
    public static final Long TEST_ID = 0l;
	public static final String TEST_USERNAME = "testUsername";
	public static final String TEST_PASSWORD = "testPassword";
	private static final Cart TEST_CART = CartTests.getTestCart();

	@Test
    public void testBean() {
    	BeanTestUtils.testBean(User.class);
    }

	public static User getTestUser() {
    	return getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_CART);
	}

	public static User getTestUser(Long id, String username, String password,
			Cart cart) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setCart(cart);
		if(cart != null) {
			cart.setUser(user);
		}
		return user;
	}
}

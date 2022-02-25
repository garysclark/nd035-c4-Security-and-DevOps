package com.example.demo.model.persistence;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class UserTests {
    private static final long TEST_ID = 0;
	private static final String TEST_USERNAME = "testUsername";
	private static final String TEST_PASSWORD = "testPassword";
	private static final Cart TEST_CART = CartTests.getTestCart();

	@Test
    public void testBean() {
    	BeanTestUtils.test(User.class);
    }

	public static User getTestUser() {
    	return getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_CART);
	}

	public static User getTestUser(long id, String username, String password,
			Cart cart) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		cart.setUser(user);
		user.setCart(cart);
		return user;
	}
}

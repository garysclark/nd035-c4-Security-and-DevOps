package com.example.demo.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    
    @Test
    public void canCreateWithAttributes() {
    	User user = getTestUser();
    	assertEquals(TEST_ID, user.getId());
    	assertEquals(TEST_USERNAME, user.getUsername());
    	assertEquals(TEST_PASSWORD, user.getPassword());
    	assertEquals(TEST_CART, user.getCart());
    }

	public static User getTestUser() {
    	return new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_CART);
	}
}

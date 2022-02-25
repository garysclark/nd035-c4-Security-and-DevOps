package com.example.demo.model.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class UserOrderTests {
	
	@Test
    public void testBean() {
    	BeanTestUtils.test(UserOrder.class);
    }

	@Test
	public void canCreateFromCart() {
		User user = UserTests.getTestUser();
		UserOrder order = UserOrder.createFromCart(user.getCart());
		assertNotNull(order);
		assertEquals(user.getCart().getItems(), order.getItems());
		assertEquals(user.getCart().getTotal(), order.getTotal());
		assertEquals(user, order.getUser());
	}
}

package com.example.demo.model.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CartTests {

	private static final Long TEST_ID = 99l;
	private static final BigDecimal TEST_TOTAL = new BigDecimal("9.99");
	
	@Mock
	private User mockUser;
	
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
	public void canCreateCart() {
		Cart cart = new Cart();
		assertNotNull(cart);
	}
	
	@Test
	public void canSetGetAttributes() {
		Cart cart = new Cart();
		cart.setId(TEST_ID);
		assertEquals(TEST_ID, cart.getId());
		cart.setTotal(TEST_TOTAL);
		assertEquals(TEST_TOTAL, cart.getTotal());
	}
	
	@Test
	public void canSetUser() {
		Cart cart = new Cart();
		User user = UserTests.getTestUser();
		cart.setUser(user);
		assertEquals(user, cart.getUser());
	}
	
	public static Cart getTestCart() {
		// TODO Auto-generated method stub
		return null;
	}

}

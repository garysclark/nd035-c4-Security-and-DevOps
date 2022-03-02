package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.CartTests;
import com.example.demo.model.persistence.repositories.CartRepository;

public class CartServiceTests {

	@Mock
	private CartRepository mockCartRepository;
	
	@InjectMocks
	private CartService cartService = new CartService();

	private AutoCloseable autoClosable;

	@BeforeEach
	public void beforeEach() {
		autoClosable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		autoClosable.close();
	}
	
	@Test
	public void canAccessService() {
		assertNotNull(cartService);
	}
	
	@Test
	public void canSaveCart() {
		Cart cart = CartTests.getTestCart();
		when(mockCartRepository.save(cart)).thenReturn(cart);
		
		Cart savedCart = cartService.SaveCart(cart);
		assertNotNull(savedCart);
	}
}

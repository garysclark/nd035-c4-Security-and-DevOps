package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.CartTests;
import com.example.demo.model.persistence.repositories.CartRepository;

@SpringBootTest
public class CartServiceTests {

	@MockBean
	private CartRepository mockCartRepository;
	
	@Autowired
	private CartService cartService = new CartService();
	
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

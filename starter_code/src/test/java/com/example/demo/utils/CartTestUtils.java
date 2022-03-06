package com.example.demo.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.requests.ModifyCartRequestTests;

public class CartTestUtils {

	private static final String HOST_URL = TestUtils.HOST_URL;
	private static final String ADD_TO_CART_ENDPOINT = CartController.ADD_TO_CART_ENDPOINT;
	public static final String REMOVE_FROM_CART_ENDPOINT = CartController.REMOVE_FROM_CART_ENDPOINT;

	private TestRestTemplate testRestTemplate;
	private int port;
	private AuthorizedUser authorizedUser;

	public CartTestUtils(TestRestTemplate testRestTemplate, int port, AuthorizedUser authorizedUser) {
		this.testRestTemplate = testRestTemplate;
		this.port = port;
		this.authorizedUser = authorizedUser;
	}

	public ResponseEntity<Cart> addItemToCart(Long itemId, int itemCount, String username) {
		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(itemId, itemCount, username);
		HttpEntity<ModifyCartRequest> entity = new HttpEntity<ModifyCartRequest>(request, getJwtEntity().getHeaders());

		ResponseEntity<Cart> response = testRestTemplate.exchange(
				HOST_URL + port + ADD_TO_CART_ENDPOINT, HttpMethod.POST, entity, 
				Cart.class);
		return response;
	}

	private HttpEntity<String> getJwtEntity() {
		return authorizedUser.getJwtEntity();
	}

	public ResponseEntity<Cart> removeItemFromCart(Long itemId, int itemCount, String username) {
		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(itemId, itemCount, username);
		HttpEntity<ModifyCartRequest> entity = new HttpEntity<ModifyCartRequest>(request, getJwtEntity().getHeaders());

		ResponseEntity<Cart> response = testRestTemplate.exchange(
				HOST_URL + port + REMOVE_FROM_CART_ENDPOINT, HttpMethod.POST, entity, 
				Cart.class);
		return response;
	}

}

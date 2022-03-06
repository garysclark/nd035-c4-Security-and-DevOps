package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.example.demo.utils.AuthorizedUser;
import com.example.demo.utils.CartTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class CartControllerIntegrationTests {

	private static final List<Item> TEST_ITEMS = ItemControllerIntegrationTests.TEST_ITEMS;

	private static final int TEST_ADD_ITEM_COUNT = 2;

	private static final String TEST_INVALID_USERNAME = "invalidUserName";

	@LocalServerPort
	private int port;

	@Autowired
	private CartController cartController;
	private AuthorizedUser authorizedUser;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private CartTestUtils cartTestUtils;

	@BeforeEach
	public void beforeEach(){
		authorizedUser = new AuthorizedUser(testRestTemplate, port);
		cartTestUtils = new CartTestUtils(testRestTemplate, port, authorizedUser);
	}
	
	@Test
	public void canAccessController() {
		assertNotNull(cartController);
	}
	
	@Test
	public void canAddToCart() throws JsonProcessingException {
		createAndLoginUser();
		ResponseEntity<Cart> response = cartTestUtils.addItemToCart(TEST_ITEMS.get(0).getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(TEST_ADD_ITEM_COUNT, cart.getItems().size());
		for(Item item : cart.getItems()) {
			assertEquals(TEST_ITEMS.get(0), item);
		}
	}
	
	@Test
	public void canHandleAddItemToCartWithInvalidUsername() throws JsonProcessingException {
		createAndLoginUser();
		ResponseEntity<Cart> response = cartTestUtils.addItemToCart(TEST_ITEMS.get(0).getId(), TEST_ADD_ITEM_COUNT, TEST_INVALID_USERNAME);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void canRemoveItemFromCart() throws JsonProcessingException {
		createAndLoginUser();
		cartTestUtils.addItemToCart(TEST_ITEMS.get(0).getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());

		ResponseEntity<Cart> response = cartTestUtils.removeItemFromCart(TEST_ITEMS.get(0).getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(0, cart.getItems().size());
	}
	
	@Test
	public void canHandleRemoveItemFromCartWithInvalidUsername() throws JsonProcessingException {
		createAndLoginUser();
		ResponseEntity<Cart> response = cartTestUtils.removeItemFromCart(TEST_ITEMS.get(0).getId(), TEST_ADD_ITEM_COUNT, TEST_INVALID_USERNAME);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	User getUser() {
		return authorizedUser.getUser();
	}

	private void createAndLoginUser() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		authorizedUser.createAndLogin(request);
	}
}

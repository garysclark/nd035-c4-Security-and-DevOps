package com.example.demo.utils;

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

import com.example.demo.controllers.ItemControllerIntegrationTests;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class CartTestUtilsTests {

	private static final List<Item> TEST_ITEMS = ItemControllerIntegrationTests.TEST_ITEMS;

	private static final int TEST_ADD_ITEM_COUNT = 1;

	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate testRestTemplate;

	private AuthorizedUser authorizedUser;

	@BeforeEach
	public void beforeEach() throws JsonProcessingException{
		authorizedUser = new AuthorizedUser(testRestTemplate, port);
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		authorizedUser.createAndLogin(request);
	}

	@Test
	public void canCreateCartUtils() {
		CartTestUtils cartUtils = new CartTestUtils(testRestTemplate, port, authorizedUser);
		assertNotNull(cartUtils);
	}
	
	@Test
	public void canAddItemToCart() {
		Item expectedItem = TEST_ITEMS.get(0);
		CartTestUtils cartUtils = new CartTestUtils(testRestTemplate, port, authorizedUser);

		ResponseEntity<Cart> response = cartUtils.addItemToCart(expectedItem.getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(TEST_ADD_ITEM_COUNT, cart.getItems().size());
		for(Item item : cart.getItems()) {
			assertEquals(expectedItem, item);
		}
	}
	
	@Test
	public void canRemoveItemFromCart() {
		Item testItem = TEST_ITEMS.get(0);
		CartTestUtils cartUtils = new CartTestUtils(testRestTemplate, port, authorizedUser);

		cartUtils.addItemToCart(testItem.getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());
		ResponseEntity<Cart> response = cartUtils.removeItemFromCart(testItem.getId(), TEST_ADD_ITEM_COUNT, getUser().getUsername());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(0, cart.getItems().size());
	}

	User getUser() {
		return authorizedUser.getUser();
	}

}

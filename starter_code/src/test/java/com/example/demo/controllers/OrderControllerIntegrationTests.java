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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.example.demo.utils.CartTestUtils;
import com.example.demo.utils.TestUtils;
import com.example.demo.utils.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class OrderControllerIntegrationTests {

	private static final String SUBMIT_ORDER_BY_USERNAME_ENDPOINT = OrderController.SUBMIT_ORDER_BY_USERNAME_ENDPOINT;

	private static final String GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT = OrderController.GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT;

	private static final List<Item> TEST_ITEMS = ItemControllerIntegrationTests.TEST_ITEMS;

	private static final int TEST_ADD_ITEM_COUNT = 2;

	private static final String HOST_URL = TestUtils.HOST_URL;

	private static final String TEST_INVALID_USERNAME = "invalidUsername";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private OrderController orderController;
	private UserTestUtils userTestUtils;
	private CartTestUtils cartTestUtils;

	@BeforeEach
	public void beforeEach(){
		userTestUtils = new UserTestUtils(testRestTemplate, port);
		cartTestUtils = new CartTestUtils(testRestTemplate, port, userTestUtils);
	}

	@Test
	public void canAccessController() {
		assertNotNull(orderController);
	}
	
	@Test
	public void canSubmitOrder() throws JsonProcessingException {
		createAndLoginUser();
		Item item = TEST_ITEMS.get(0);

		ResponseEntity<UserOrder> response = submitOrder(item, TEST_ADD_ITEM_COUNT, getUser().getUsername());
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		UserOrder jsonOrder = response.getBody();
		validateOrder(item, jsonOrder);
	}
	
	@Test
	public void canHandleSubmitOrderWithInvalidUserName() throws JsonProcessingException {
		createAndLoginUser();
		Item item = TEST_ITEMS.get(0);

		ResponseEntity<UserOrder> response = submitOrder(item, TEST_ADD_ITEM_COUNT, TEST_INVALID_USERNAME);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void canGetOrderHistoryByUsername() throws JsonProcessingException {
		createAndLoginUser();
		Item item = TEST_ITEMS.get(0);
		submitOrder(item, TEST_ADD_ITEM_COUNT, getUser().getUsername());

		ResponseEntity<List<UserOrder>> response = getOrderHistory(getUser().getUsername());
		
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<UserOrder> jsonOrders = response.getBody();
		assertEquals(1, jsonOrders.size());
		validateOrder(item, jsonOrders.get(0));
	}
	
	@Test
	public void canHandleGetOrderHistoryByInvalidUserName() throws JsonProcessingException {
		createAndLoginUser();

		ResponseEntity<List<UserOrder>> response = getOrderHistory(TEST_INVALID_USERNAME);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	private ResponseEntity<List<UserOrder>> getOrderHistory(String username) {
		ParameterizedTypeReference<List<UserOrder>> responseType = new ParameterizedTypeReference<List<UserOrder>>() {};
		return testRestTemplate.exchange(
				HOST_URL + port + GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT + username, 
				HttpMethod.GET, userTestUtils.getJwtEntity(), 
				responseType);
	}

	private ResponseEntity<UserOrder> submitOrder(Item item, int itemCount, String username){
		addItemToCart(item, itemCount);
		
		return testRestTemplate.exchange(
				HOST_URL + port + SUBMIT_ORDER_BY_USERNAME_ENDPOINT + username, 
				HttpMethod.POST, userTestUtils.getJwtEntity(), 
				UserOrder.class);
	}

	private void validateOrder(Item expectedItem, UserOrder jsonOrder) {
		assertNotNull(jsonOrder);
		assertEquals(getUser(), jsonOrder.getUser());
		assertEquals(TEST_ADD_ITEM_COUNT, jsonOrder.getItems().size());
		for(Item item : jsonOrder.getItems()) {
			assertEquals(expectedItem, item);
		}
	}
	
	private ResponseEntity<Cart> addItemToCart(Item item, int itemCount) {
		return cartTestUtils.addItemToCart(item.getId(), itemCount, getUser().getUsername());
	}

	private void createAndLoginUser() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		userTestUtils.createAndLoginUser(request);
	}

	User getUser() {
		return userTestUtils.getUser();
	}
}

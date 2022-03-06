package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.CreateUserRequestTests;
import com.example.demo.utils.UserTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional
public class ItemControllerIntegrationTests {

	public static final List<Item> TEST_ITEMS = Arrays.asList(
			new Item(1l,"Round Widget", new BigDecimal("2.99"), "A widget that is round"),
			new Item(2l, "Square Widget", new BigDecimal("1.99"), "A widget that is square"));

	private static final int TEST_ID = 1;

	private static final int TEST_BY_NAME_ID = 2;

	private static final String TEST_INVALID_USERNAME = "invalidUserName";

	private static final Long TEST_INVALID_ID = 99l;

	@LocalServerPort
	private int port;

	@Autowired
	private ItemController itemController;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private UserTestUtils userTestUtils;

	@BeforeEach
	public void beforeEach() {
		userTestUtils = new UserTestUtils(testRestTemplate, port);
	}
	
	@Test
	public void canAccessController() {
		assertNotNull(itemController);
	}
	
	@Test
	public void canGetItems() throws JsonProcessingException {
		createAndLoginUser();
		ParameterizedTypeReference<List<Item>> responseType = new ParameterizedTypeReference<List<Item>>() {};

		ResponseEntity<List<Item>> response = testRestTemplate.exchange("http://localhost:" + port + "/api/item", HttpMethod.GET, getJwtEntity(),
				responseType);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Item> items = response.getBody();
		assertNotNull(items);
		validateItems(items);
	}
	
	@Test
	public void canGetItemById() throws JsonProcessingException {
		createAndLoginUser();

		ResponseEntity<Item> response = testRestTemplate.exchange("http://localhost:" + port + "/api/item/" + TEST_ID, HttpMethod.GET, getJwtEntity(),
				Item.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Item item = response.getBody();
		validateItem(TEST_ITEMS.get(TEST_ID - 1), item);
	}
	
	@Test
	public void canHandleGetItemByInvalidId() throws JsonProcessingException {
		createAndLoginUser();

		ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/api/item/" + TEST_INVALID_ID, HttpMethod.GET, getJwtEntity(),
				String.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void canGetItemByName() throws JsonProcessingException {
		createAndLoginUser();
		ParameterizedTypeReference<List<Item>> responseType = new ParameterizedTypeReference<List<Item>>() {};

		ResponseEntity<List<Item>> response = testRestTemplate.exchange("http://localhost:" + port + "/api/item/name/" + TEST_ITEMS.get(TEST_BY_NAME_ID - 1).getName(), HttpMethod.GET, getJwtEntity(),
				responseType);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Item> items = response.getBody();
		validateItem(TEST_ITEMS.get(TEST_BY_NAME_ID - 1), items.get(0));

	}

	@Test
	public void canHandlGetItemByInvalidName() throws JsonProcessingException {
		createAndLoginUser();
		ParameterizedTypeReference<List<Item>> responseType = new ParameterizedTypeReference<List<Item>>() {};

		ResponseEntity<List<Item>> response = testRestTemplate.exchange("http://localhost:" + port + "/api/item/name/" + TEST_INVALID_USERNAME, HttpMethod.GET, getJwtEntity(),
				responseType);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	private void validateItems(List<Item> items) {
		assertEquals(TEST_ITEMS.size(), items.size());
		for(int i = 0; i < TEST_ITEMS.size(); i++) {
			validateItem(TEST_ITEMS.get(i), items.get(i));
		}
	}

	private void validateItem(Item item1, Item item2) {
		assertEquals(item1, item2);
	}

	private HttpEntity<String> getJwtEntity() {
		return userTestUtils.getJwtEntity();
	}

	private void createAndLoginUser() throws JsonProcessingException {
		CreateUserRequest request = CreateUserRequestTests.getTestCreateUserRequest();
		userTestUtils.createAndLoginUser(request);
	}
}

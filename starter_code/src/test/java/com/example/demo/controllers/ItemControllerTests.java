package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.ItemTests;
import com.example.demo.services.ItemService;
import com.example.demo.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTests {

	private static final List<Item> TEST_ITEMS = Collections.singletonList(ItemTests.getTestItem());
	private static final String GET_ALL_ITEMS_ENDPOINT = ItemController.GET_ALL_ITEMS_ENDPOINT;
	private static final String GET_ITEM_BY_ID_ENDPOINT = ItemController.GET_ITEM_BY_ID_ENDPOINT;
	private static final String GET_ITEMS_BY_NAME_ENDPOINT = ItemController.GET_ITEMS_BY_NAME_ENDPOINT;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ItemController itemController;

	@MockBean
	private ItemService mockItemService;

	@Test
	public void canAccessController() {
		assertNotNull(itemController);
	}
	
	@WithMockUser
	@Test
	public void canGetItems() throws Exception {
		BDDMockito.given(mockItemService.findAllItems()).willReturn(TEST_ITEMS);

		ResultActions resultActions = performGetAction(GET_ALL_ITEMS_ENDPOINT);
		resultActions.andExpect(status().isOk());
		
		validateItems(TEST_ITEMS, resultActions);
	}
	
	@WithMockUser
	@Test
	public void canGetItemById() throws Exception {
		Item item = TEST_ITEMS.get(0);
		BDDMockito.given(mockItemService.findItemById(item.getId())).willReturn(item);

		ResultActions resultActions = performGetAction(GET_ITEM_BY_ID_ENDPOINT + item.getId());
		resultActions.andExpect(status().isOk());
		
		validateItem(item, resultActions);
	}
	
	@WithMockUser
	@Test
	public void canGetItemsByName() throws Exception {
		String itemName = TEST_ITEMS.get(0).getName();
		BDDMockito.given(mockItemService.findItemsByName(itemName)).willReturn(TEST_ITEMS);

		ResultActions resultActions = performGetAction(GET_ITEMS_BY_NAME_ENDPOINT + itemName);
		resultActions.andExpect(status().isOk());
		
		validateItems(TEST_ITEMS, resultActions);
	}

	@WithMockUser
	@Test
	public void canHandleGetItemsByInvalidName() throws Exception {
		String itemName = TEST_ITEMS.get(0).getName();
		BDDMockito.given(mockItemService.findItemsByName(itemName)).willReturn(new ArrayList<Item>());

		ResultActions resultActions = performGetAction(GET_ITEMS_BY_NAME_ENDPOINT + itemName);
		resultActions.andExpect(status().isNotFound());
	}

	private ResultActions performGetAction(String rawPath) throws Exception, URISyntaxException {
		ResultActions resultActions = mockMvc.perform(
				get(TestUtils.getUri(rawPath))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		return resultActions;
	}

	private void validateItems(List<Item> expectedItems, ResultActions resultActions)
			throws UnsupportedEncodingException, JsonProcessingException, JsonMappingException {
		String contentAsString = validateContentPresent(resultActions);
		List<Item> jsonItems = new ObjectMapper().readValue(contentAsString, new TypeReference<List<Item>>(){});
		assertEquals(expectedItems, jsonItems);
	}

	private void validateItem(Item expectedItem, ResultActions resultActions)
			throws UnsupportedEncodingException, JsonProcessingException, JsonMappingException {
		String contentAsString = validateContentPresent(resultActions);
		Item jsonItem = new ObjectMapper().readValue(contentAsString, Item.class);
		assertEquals(expectedItem, jsonItem);
	}

	private String validateContentPresent(ResultActions resultActions) throws UnsupportedEncodingException {
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		return contentAsString;
	}
}

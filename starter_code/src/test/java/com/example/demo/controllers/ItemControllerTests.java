package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.net.URI;
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

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTests {

	private static final List<Item> TEST_ITEMS = Collections.singletonList(ItemTests.getTestItem());
	private static final String FIND_ALL_ITEMS_ENDPOINT = "/api/item";
	private static final String FIND_ITEM_BY_ID_ENDPOINT = "/api/item/";
	private static final String FIND_ITEMS_BY_NAME_ENDPOINT = "/api/item/name/";

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

		ResultActions resultActions = performGetAction(FIND_ALL_ITEMS_ENDPOINT);
		resultActions.andExpect(status().isOk());
		
		validateResponse(resultActions, "$[0].", TEST_ITEMS.get(0));
	}
	
	@WithMockUser
	@Test
	public void canGetItemById() throws Exception {
		Item item = TEST_ITEMS.get(0);
		BDDMockito.given(mockItemService.findItemById(item.getId())).willReturn(item);

		ResultActions resultActions = performGetAction(FIND_ITEM_BY_ID_ENDPOINT + item.getId());
		resultActions.andExpect(status().isOk());
		
		validateResponse(resultActions, "$.", TEST_ITEMS.get(0));
	}
	
	@WithMockUser
	@Test
	public void canGetItemsByName() throws Exception {
		String itemName = TEST_ITEMS.get(0).getName();
		BDDMockito.given(mockItemService.findItemsByName(itemName)).willReturn(TEST_ITEMS);

		ResultActions resultActions = performGetAction(FIND_ITEMS_BY_NAME_ENDPOINT + itemName);
		resultActions.andExpect(status().isOk());
		
		validateResponse(resultActions, "$[0].", TEST_ITEMS.get(0));
	}

	@WithMockUser
	@Test
	public void canHandleGetItemsByInvalidName() throws Exception {
		String itemName = TEST_ITEMS.get(0).getName();
		BDDMockito.given(mockItemService.findItemsByName(itemName)).willReturn(new ArrayList<Item>());

		ResultActions resultActions = performGetAction(FIND_ITEMS_BY_NAME_ENDPOINT + itemName);
		resultActions.andExpect(status().isNotFound());
	}

	@WithMockUser
	@Test
	public void canHandleGetItemsByInvalidNameNullResponse() throws Exception {
		String itemName = TEST_ITEMS.get(0).getName();
		BDDMockito.given(mockItemService.findItemsByName(itemName)).willReturn(null);

		ResultActions resultActions = performGetAction(FIND_ITEMS_BY_NAME_ENDPOINT + itemName);
		resultActions.andExpect(status().isNotFound());
	}

	private ResultActions performGetAction(String rawPath) throws Exception, URISyntaxException {
		// the following accounts for spaces in the URL (specifically the item name)
		String escapedPath = new URI("dummy",rawPath, null).getRawSchemeSpecificPart();

		ResultActions resultActions = mockMvc.perform(
				get(new URI(escapedPath))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		return resultActions;
	}

	private void validateResponse(ResultActions resultActions, String responsePrefix, Item item) throws UnsupportedEncodingException, Exception {
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		resultActions.andExpect(jsonPath(responsePrefix + "id").value(item.getId()));
		resultActions.andExpect(jsonPath(responsePrefix + "name").value(item.getName()));
		resultActions.andExpect(jsonPath(responsePrefix + "description").value(item.getDescription()));
	}
}

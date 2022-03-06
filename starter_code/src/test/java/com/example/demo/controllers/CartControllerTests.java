package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.CartTests;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.ItemTests;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.requests.ModifyCartRequestTests;
import com.example.demo.services.CartService;
import com.example.demo.services.ItemService;
import com.example.demo.services.UserService;
import com.example.demo.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CartControllerTests {

	private static final Long TEST_ID = 0l;
	private static final String TEST_USERNAME = "testusername";
	private static final String TEST_PASSWORD = "testpassword";
	private static final String ADD_TO_CART_ENDPOINT = "/api/cart/addToCart";
	private static final String REMOVE_FROM_CART_ENDPOINT = "/api/cart/removeFromCart";


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JacksonTester<ModifyCartRequest> json;

	@Autowired
	private CartController cartController;
	@MockBean
	private UserService mockUserService;
	@MockBean
	private ItemService mockItemService;
	@MockBean
	private CartService mockCartService;

	@Test
	public void canAccessController() {
		assertNotNull(cartController);
	}
	
	@Test
	@WithMockUser
	public void canAddItemToCart() throws URISyntaxException, IOException, Exception {
		Item newItem = ItemTests.getTestItem();
		newItem.setId(newItem.getId() + 1);
		Cart expectedCart = CartTests.getTestCart();
		expectedCart.addItem(newItem);
		
		Cart existingCart = CartTests.getTestCart();
		User user = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, existingCart);
		expectedCart.setUser(user);

		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(newItem.getId(), 1, TEST_USERNAME);

		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(user);
		BDDMockito.given(mockItemService.findItemById(newItem.getId())).willReturn(newItem);
		BDDMockito.given(mockCartService.SaveCart(existingCart)).willReturn(existingCart);

		ResultActions resultActions = performPostAction(request, ADD_TO_CART_ENDPOINT);
		resultActions.andExpect(status().isOk());
		
		validateResponse(resultActions, "$.", expectedCart);
	}
	
	@Test
	@WithMockUser
	public void canHandleAddItemForInvalidUser() throws URISyntaxException, IOException, Exception {
		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(1l, 1, TEST_USERNAME);

		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(null);

		ResultActions resultActions = performPostAction(request, ADD_TO_CART_ENDPOINT);
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void canRemoveItemFromCart() throws URISyntaxException, IOException, Exception {
		Cart expectedCart = CartTests.getTestCart();
		Item itemToRemove = expectedCart.getItems().get(0);
		expectedCart.removeItem(itemToRemove);
		
		Cart cart = CartTests.getTestCart();
		User user = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, cart);
		expectedCart.setUser(user);

		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(itemToRemove.getId(), 1, TEST_USERNAME);

		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(user);
		BDDMockito.given(mockItemService.findItemById(itemToRemove.getId())).willReturn(itemToRemove);
		BDDMockito.given(mockCartService.SaveCart(cart)).willReturn(cart);

		ResultActions resultActions = performPostAction(request, REMOVE_FROM_CART_ENDPOINT);
		resultActions.andExpect(status().isOk());
		
		validateResponse(resultActions, "$.", expectedCart);
	}
	
	@Test
	@WithMockUser
	public void canHandleRemoveItemForInvalidUser() throws URISyntaxException, IOException, Exception {
		ModifyCartRequest request = ModifyCartRequestTests.getTestModifyCartRequest(1l, 1, TEST_USERNAME);

		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(null);

		ResultActions resultActions = performPostAction(request, REMOVE_FROM_CART_ENDPOINT);
		resultActions.andExpect(status().isNotFound());
	}

	private ResultActions performPostAction(ModifyCartRequest request, String endPoint)
			throws Exception, URISyntaxException, IOException {
		ResultActions resultActions = mockMvc.perform(
				post(TestUtils.getUri(endPoint))
				.content(json.write(request).getJson())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		return resultActions;
	}
	

	private void validateResponse(ResultActions resultActions, String responsePrefix, Cart cart) throws UnsupportedEncodingException, Exception {
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		Cart jsonCart = new ObjectMapper().readValue(contentAsString, Cart.class);
		assertEquals(cart.getId(), jsonCart.getId());
		assertEquals(cart.getItems(), jsonCart.getItems());
		assertEquals(cart.getTotal(), jsonCart.getTotal());
		assertEquals(cart.getUser().getId(), jsonCart.getUser().getId());
		assertEquals(cart.getUser().getUsername(), jsonCart.getUser().getUsername());
	}


}

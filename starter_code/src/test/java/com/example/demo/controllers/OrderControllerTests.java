package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
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
import org.springframework.test.web.servlet.ResultMatcher;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.services.OrderService;
import com.example.demo.services.UserService;
import com.example.demo.utils.TestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTests {

	private static final String SUBMIT_ORDER_ENDPOINT = "/api/order/submit/";

	private static final String GET_ORDER_HISTORY_ENDPOINT = "/api/order/history/";

	private static final String TEST_INVALID_USER_NAME = "invlaidUser";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderController orderController;

	@MockBean
	private OrderService mockOrderService;

	@MockBean
	private UserService mockUserService;

	@Test
	public void canAccessController() {
		assertNotNull(orderController);
	}

	@Test
	@WithMockUser
	public void canSubmitOrder() throws URISyntaxException, Exception {
		User user = UserTests.getTestUser();
		UserOrder expectedOrder = UserOrder.createFromCart(user.getCart());
		BDDMockito.given(mockUserService.findUserByUserName(user.getUsername())).willReturn(user);
		BDDMockito.given(mockOrderService.saveOrder(expectedOrder)).willReturn(expectedOrder);

		ResultActions resultActions = performPostAction(SUBMIT_ORDER_ENDPOINT + user.getUsername(), status().isOk());
		
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		UserOrder jsonOrder = new ObjectMapper().readValue(contentAsString, UserOrder.class);
		assertEquals(convertToJsonOrder(expectedOrder), jsonOrder);
	}

	@Test
	@WithMockUser
	public void canHandleSubmitOrderWithInvalidUser() throws URISyntaxException, Exception {
		BDDMockito.given(mockUserService.findUserByUserName(TEST_INVALID_USER_NAME)).willReturn(null);

		performPostAction(SUBMIT_ORDER_ENDPOINT + TEST_INVALID_USER_NAME, status().isNotFound());
	}

	@Test
	@WithMockUser
	public void canGetOrdersForUser() throws URISyntaxException, Exception {
		User user = UserTests.getTestUser();
		List<UserOrder> expectedOrders = Collections.singletonList(UserOrder.createFromCart(user.getCart()));
		BDDMockito.given(mockUserService.findUserByUserName(user.getUsername())).willReturn(user);
		BDDMockito.given(mockOrderService.findOrdersByUser(user)).willReturn(expectedOrders);

		ResultActions resultActions = performGetAction(GET_ORDER_HISTORY_ENDPOINT + user.getUsername(), status().isOk());

		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		List<UserOrder> jsonOrders = new ObjectMapper().readValue(contentAsString, new TypeReference<List<UserOrder>>(){});
		assertNotNull(jsonOrders);
		assertEquals(convertToJsonOrders(expectedOrders), jsonOrders);
	}

	private List<UserOrder> convertToJsonOrders(List<UserOrder> userOrders) {
		for(UserOrder userOrder : userOrders) {
			convertToJsonOrder(userOrder);
		}
		return userOrders;
	}

	@Test
	@WithMockUser
	public void canHandleGetOrdersForInvalidUser() throws URISyntaxException, Exception {
		BDDMockito.given(mockUserService.findUserByUserName(TEST_INVALID_USER_NAME)).willReturn(null);

		performGetAction(GET_ORDER_HISTORY_ENDPOINT + TEST_INVALID_USER_NAME, status().isNotFound());
	}

	private ResultActions performPostAction(String path, ResultMatcher status) throws Exception, URISyntaxException {
		return mockMvc.perform(
				post(TestUtils.getUri(path))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status);
	}

	private ResultActions performGetAction(String path, ResultMatcher status) throws Exception, URISyntaxException {
		return mockMvc.perform(
				get(TestUtils.getUri(path))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status);
	}

	private UserOrder convertToJsonOrder(UserOrder userOrder) {
		userOrder.getUser().setCart(null);
		userOrder.getUser().setPassword(null);
		return userOrder;
	}
}

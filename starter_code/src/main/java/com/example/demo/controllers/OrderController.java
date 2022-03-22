package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.services.OrderService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping(OrderController.API_ORDER_ENDPOINT)
public class OrderController {
	
	
	private static final String LOG_INFO_ORDER_HISTORY_REQUEST_SUCCESS = "ORDER HISTORY REQUEST - SUCCESS - Retrived order history for user {}";

	private static final String LOG_ERROR_ORDER_HISTORY_REQUEST_FAILED_INVALID_USER = "ORDER HISTORY REQUEST - FAILED - Order history not retrieved - Invalid user {}";

	private static final String LOG_INFO_GETTING_ORDER_HISTORY_FOR_USER = "Getting order history for user {}";

	private static final String LOG_INFO_SUBMIT_ORDER_REQUEST_SUCCESS = "SUBMIT ORDER REQUEST - SUCCESS - Order submitted for user {}";

	private static final String LOG_ERROR_SUBMIT_ORDER_REQUEST_FAILED_INVALID_USER = "SUBMIT ORDER REQUEST - FAILED - Order not submitted - Invalid user {}";

	private static final String LOG_INFO_SUBMITTING_AN_ORDER_FOR_USER = "Submitting an order for user {}";

	private static final String GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT_PART = "/history/{username}";

	private static final String SUBMIT_ORDER_BY_USERNAME_ENDPOINT_PART = "/submit/{username}";

	public static final String API_ORDER_ENDPOINT = "/api/order";
	
	public static final String GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT = API_ORDER_ENDPOINT + "/history/";

	public static final String SUBMIT_ORDER_BY_USERNAME_ENDPOINT = API_ORDER_ENDPOINT + "/submit/";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	
	@PostMapping(SUBMIT_ORDER_BY_USERNAME_ENDPOINT_PART)
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userService.findUserByUserName(username);
		logger.info(LOG_INFO_SUBMITTING_AN_ORDER_FOR_USER, username);
		if(user == null) {
			logger.error(LOG_ERROR_SUBMIT_ORDER_REQUEST_FAILED_INVALID_USER, username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		UserOrder savedUserOrder = orderService.saveOrder(order);
		logger.info(LOG_INFO_SUBMIT_ORDER_REQUEST_SUCCESS, username);
		return ResponseEntity.ok(savedUserOrder);
	}
	
	@GetMapping(GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT_PART)
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userService.findUserByUserName(username);
		logger.info(LOG_INFO_GETTING_ORDER_HISTORY_FOR_USER, username);
		if(user == null) {
			logger.error(LOG_ERROR_ORDER_HISTORY_REQUEST_FAILED_INVALID_USER, username);
			return ResponseEntity.notFound().build();
		}
		logger.info(LOG_INFO_ORDER_HISTORY_REQUEST_SUCCESS, username);
		return ResponseEntity.ok(orderService.findOrdersByUser(user));
	}
}

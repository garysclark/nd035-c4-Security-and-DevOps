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
		logger.info("Submitting an order for user {}", username);
		if(user == null) {
			logger.error("Order not submitted - Invalid user {}", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		UserOrder savedUserOrder = orderService.saveOrder(order);
		return ResponseEntity.ok(savedUserOrder);
	}
	
	@GetMapping(GET_ORDER_HISTORY_BY_USERNAME_ENDPOINT_PART)
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userService.findUserByUserName(username);
		logger.info("Getting order history for user {}", username);
		if(user == null) {
			logger.error("Order history not retrieved - Invalid user {}", username);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderService.findOrdersByUser(user));
	}
}

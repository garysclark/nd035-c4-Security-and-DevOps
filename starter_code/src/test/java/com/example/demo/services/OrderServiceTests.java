package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.model.persistence.repositories.OrderRepository;

@SpringBootTest
public class OrderServiceTests {

	@MockBean
	private OrderRepository orderRepositoy;

	@Autowired
	private OrderService orderService = new OrderService();
	
	@Test
	public void canAccessService() {
		assertNotNull(orderService);
	}
	
	@Test
	public void canSaveOrder() {
		UserOrder order = UserOrder.createFromCart(UserTests.getTestUser().getCart());
		when(orderRepositoy.save(order)).thenReturn(order);

		UserOrder savedOrder = orderService.saveOrder(order);
		assertEquals(order, savedOrder);
	}
	
	@Test
	public void canFindOrderByUser() {
		User user = UserTests.getTestUser();
		List<UserOrder> orders = Collections.singletonList(UserOrder.createFromCart(user.getCart()));
		when(orderRepositoy.findByUser(user)).thenReturn(orders);
		
		List<UserOrder> foundOrders = orderService.findOrdersByUser(user);
		
		assertEquals(orders, foundOrders);
	}
}

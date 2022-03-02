package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public UserOrder saveOrder(UserOrder order) {
		return orderRepository.save(order);
	}

	public List<UserOrder> findOrdersByUser(User user) {
		return orderRepository.findByUser(user);
	}

}

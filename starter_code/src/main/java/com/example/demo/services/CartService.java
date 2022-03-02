package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;

@Service
@Transactional
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	public Cart SaveCart(Cart cart) {
		return cartRepository.save(cart);
	}

}

package com.example.demo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;

	public User saveUser(User user) {
		Cart cart = new Cart();
		cart.setUser(user);
		Cart savedCart = cartRepository.save(cart);
		user.setCart(savedCart);
		return userRepository.save(user);
	}

	public User findUserById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if(optionalUser.isEmpty()) {
			return null;
		}
		return optionalUser.get();
	}

	public User findUserByUserName(String username) {
		return userRepository.findByUsername(username);
	}

}

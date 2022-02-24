package com.example.demo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@Service
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

	public User findUserById(long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		return optionalUser.get();
	}

}

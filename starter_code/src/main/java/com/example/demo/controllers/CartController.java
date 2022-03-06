package com.example.demo.controllers;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.services.CartService;
import com.example.demo.services.ItemService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping(CartController.API_CART_ENDPOINT)
public class CartController {


	private static final String ADD_TO_CART_ENDPOINT_PART = "/addToCart";

	private static final String REMOVE_FROM_CART_ENDPOINT_PART = "/removeFromCart";

	public static final String API_CART_ENDPOINT = "/api/cart";

	public static final String ADD_TO_CART_ENDPOINT = API_CART_ENDPOINT + ADD_TO_CART_ENDPOINT_PART;
	
	public static final String REMOVE_FROM_CART_ENDPOINT = API_CART_ENDPOINT + REMOVE_FROM_CART_ENDPOINT_PART;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private ItemService itemService;
	
	@PostMapping(ADD_TO_CART_ENDPOINT_PART)
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userService.findUserByUserName(request.getUsername());
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Item item = itemService.findItemById(request.getItemId());
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item));
		Cart savedCart = cartService.SaveCart(cart);
		return ResponseEntity.ok(savedCart);
	}
	
	@PostMapping(REMOVE_FROM_CART_ENDPOINT_PART)
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userService.findUserByUserName(request.getUsername());
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Item item = itemService.findItemById(request.getItemId());
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item));
		Cart savedCart = cartService.SaveCart(cart);
		return ResponseEntity.ok(savedCart);
	}
		
}

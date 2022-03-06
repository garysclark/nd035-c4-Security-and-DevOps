package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.UserService;

@RestController
@RequestMapping(UserController.API_USER_ENDPOINT)
public class UserController {
	
	private static final String CREATE_USER_ENDPOINT_PART = "/create";

	private static final String FIND_USER_BY_USERNAME_ENDPOINT_PART = "/{username}";

	private static final String FIND_USER_BY_ID_ENDPOINT_PART = "/id/{id}";

	public static final String API_USER_ENDPOINT = "/api/user";
	
	public static final String CREATE_USER_ENDPOINT = API_USER_ENDPOINT + CREATE_USER_ENDPOINT_PART;

	public static final String FIND_USER_BY_USERNAME_ENDPOINT = API_USER_ENDPOINT + "/";

	public static final String FIND_USER_BY_ID_ENDPOINT = API_USER_ENDPOINT + "/id/";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserService userService;

	@GetMapping(FIND_USER_BY_ID_ENDPOINT_PART)
	public ResponseEntity<User> findById(@PathVariable Long id) {
		User user = userService.findUserById(id);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping(FIND_USER_BY_USERNAME_ENDPOINT_PART)
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userService.findUserByUserName(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping(CREATE_USER_ENDPOINT_PART)
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("Creating user {}", createUserRequest.getUsername());
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		
		if(createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.error("Password error.  Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		
		User savedUser = userService.saveUser(user);
		return ResponseEntity.ok(savedUser);
	}
	
}

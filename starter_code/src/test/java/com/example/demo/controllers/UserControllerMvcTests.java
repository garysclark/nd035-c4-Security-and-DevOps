package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserTests;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerMvcTests {
	private static final String TEST_USERNAME = "testusername";
	private static final String TEST_PASSWORD = "testpassword";
	private static final String TEST_ENCRYPTED_PASSWORD = "testencryptedpassword";
	private static final Long TEST_ID = 1l;
	private static final Long TEST_INVALID_ID = 2l;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JacksonTester<CreateUserRequest> json;
	
	@Autowired
	private UserController userController;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private BCryptPasswordEncoder mockBCryptPasswordEncoder;

	@Test
	public void canAccessController() {
		assertNotNull(userController);
	}

	@Test
	public void canCreateUser() throws URISyntaxException, IOException, Exception {
		User user = UserTests.getTestUser(null, TEST_USERNAME, TEST_ENCRYPTED_PASSWORD, null);
		User savedUser = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, new Cart());
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(TEST_USERNAME);
		request.setPassword(TEST_PASSWORD);
		request.setConfirmPassword(TEST_PASSWORD);

		BDDMockito.given(mockUserService.saveUser(user)).willReturn(savedUser);
		BDDMockito.given(mockBCryptPasswordEncoder.encode(TEST_PASSWORD)).willReturn(TEST_ENCRYPTED_PASSWORD);

		ResultActions resultActions = mockMvc.perform(
				post(new URI("/api/user/create"))
				.content(json.write(request).getJson())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		resultActions.andExpect(jsonPath("$.id").value(TEST_ID));
		resultActions.andExpect(jsonPath("$.username").value(request.getUsername()));
	}
	
	@WithMockUser
	@Test
	public void canFindUserById() throws URISyntaxException, IOException, Exception {
		User savedUser = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, new Cart());

		BDDMockito.given(mockUserService.findUserById(TEST_ID)).willReturn(savedUser);

		ResultActions resultActions = mockMvc.perform(
				get(new URI("/api/user/id/" + TEST_ID))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		resultActions.andExpect(jsonPath("$.id").value(savedUser.getId()));
		resultActions.andExpect(jsonPath("$.username").value(savedUser.getUsername()));
	}
	
	@WithMockUser
	@Test
	public void canHandleFindUserByInvalidId() throws URISyntaxException, Exception {
		BDDMockito.given(mockUserService.findUserById(TEST_INVALID_ID)).willReturn(null);

		mockMvc.perform(
				get(new URI("/api/user/id/" + TEST_ID))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}

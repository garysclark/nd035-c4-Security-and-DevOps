package com.example.demo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.persistence.EntityNotFoundException;

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
import com.example.demo.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTests {
	private static final String FIND_USER_BY_ID_ENDPOINT = UserController.FIND_USER_BY_ID_ENDPOINT;
	private static final String CREATE_USER_ENDPOINT = UserController.CREATE_USER_ENDPOINT;
	private static final String TEST_USERNAME = "testUsername";
	private static final String TEST_PASSWORD = "testPassword";
	private static final String TEST_UNMATCHED_PASSWORD = "testUnmatchedPassword";
	private static final String TEST_ENCRYPTED_PASSWORD = "testEncryptedPassword";
	private static final Long TEST_ID = 1l;
	private static final String TEST_INVALID_PASSWORD = "badpwd";
	private static final String FIND_USER_BY_USERNAME_ENDPOINT = "/api/user/";

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
		BDDMockito.given(mockBCryptPasswordEncoder.encode(TEST_PASSWORD)).willReturn(TEST_ENCRYPTED_PASSWORD);
		BDDMockito.given(mockUserService.saveUser(user)).willReturn(savedUser);

		CreateUserRequest request = createUserRequest(TEST_USERNAME, TEST_PASSWORD, TEST_PASSWORD);
		ResultActions resultActions = performPostAction(request, CREATE_USER_ENDPOINT);
		resultActions.andExpect(status().isOk());

		validateUserResponse(savedUser, resultActions);
	}

	@Test
	public void canHandleInvalidPassword() throws URISyntaxException, IOException, Exception {
		CreateUserRequest request = createUserRequest(TEST_USERNAME, TEST_INVALID_PASSWORD, TEST_INVALID_PASSWORD);
		ResultActions resultActions = performPostAction(request, CREATE_USER_ENDPOINT);
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void canHandleUnmatchedPassword() throws URISyntaxException, IOException, Exception {
		CreateUserRequest request = createUserRequest(TEST_USERNAME, TEST_PASSWORD, TEST_UNMATCHED_PASSWORD);
		ResultActions resultActions = performPostAction(request, CREATE_USER_ENDPOINT);
		resultActions.andExpect(status().isBadRequest());
	}

	@WithMockUser
	@Test
	public void canFindUserById() throws URISyntaxException, IOException, Exception {
		User savedUser = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, new Cart());
		BDDMockito.given(mockUserService.findUserById(TEST_ID)).willReturn(savedUser);

		ResultActions resultActions = performGetAction(FIND_USER_BY_ID_ENDPOINT + TEST_ID);
		resultActions.andExpect(status().isOk());

		validateUserResponse(savedUser, resultActions);
	}

	@WithMockUser
	@Test
	public void canHandleFindUserByInvalidId() throws URISyntaxException, Exception {
		BDDMockito.given(mockUserService.findUserById(TEST_ID)).willThrow(EntityNotFoundException.class);

		ResultActions resultActions = performGetAction(FIND_USER_BY_ID_ENDPOINT + TEST_ID);
		resultActions.andExpect(status().isNotFound());
	}

	@WithMockUser
	@Test
	public void canFindUserByUserName() throws URISyntaxException, Exception {
		User savedUser = UserTests.getTestUser(TEST_ID, TEST_USERNAME, TEST_PASSWORD, new Cart());

		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(savedUser);

		ResultActions resultActions = performGetAction(FIND_USER_BY_USERNAME_ENDPOINT + TEST_USERNAME);
		resultActions.andExpect(status().isOk());

		validateUserResponse(savedUser, resultActions);
	}

	@WithMockUser
	@Test
	public void canHandleFindByInvalidUserName() throws URISyntaxException, Exception {
		BDDMockito.given(mockUserService.findUserByUserName(TEST_USERNAME)).willReturn(null);

		ResultActions resultActions = performGetAction(FIND_USER_BY_USERNAME_ENDPOINT + TEST_USERNAME);
		resultActions.andExpect(status().isNotFound());
	}

	private CreateUserRequest createUserRequest(String username, String password, String confirmPassword) {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setConfirmPassword(confirmPassword);
		return request;
	}

	private ResultActions performPostAction(CreateUserRequest request, String endPoint)
			throws Exception, URISyntaxException, IOException {
		ResultActions resultActions = mockMvc.perform(
				post(TestUtils.getUri(endPoint))
				.content(json.write(request).getJson())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		return resultActions;
	}

	private ResultActions performGetAction(String endPoint) throws Exception, URISyntaxException {
		ResultActions resultActions = mockMvc.perform(
				get(TestUtils.getUri(endPoint))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		return resultActions;
	}

	private void validateUserResponse(User user, ResultActions resultActions)
			throws UnsupportedEncodingException, Exception {
		assertNotNull(resultActions);
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		User jsonUser = new ObjectMapper().readValue(contentAsString, User.class);
		assertEquals(user.getId(), jsonUser.getId());
		assertEquals(user.getUsername(), jsonUser.getUsername());
	}
}

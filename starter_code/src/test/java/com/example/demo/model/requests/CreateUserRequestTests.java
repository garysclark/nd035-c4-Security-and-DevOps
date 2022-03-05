package com.example.demo.model.requests;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class CreateUserRequestTests {
	
	private static final String TEST_USER_NAME = "testUsername";
	private static final String TEST_PASSWORD = "testPassword";

	@Test
    public void testBean() {
    	BeanTestUtils.testSimpleBean(CreateUserRequest.class);
    }

	public static CreateUserRequest getTestCreateUserRequest() {
		return getTestCreateUserRequest(TEST_USER_NAME, TEST_PASSWORD, TEST_PASSWORD);
	}

	public static CreateUserRequest getTestCreateUserRequest(String username, String password,
			String confirmPassword) {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setConfirmPassword(confirmPassword);
		return request;
	}
}

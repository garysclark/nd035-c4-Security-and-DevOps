package com.example.demo.model.requests;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class ModifyCartRequestTests {
	
	@Test
    public void testBean() {
    	BeanTestUtils.testSimpleBean(ModifyCartRequest.class);
    }

	public static ModifyCartRequest getTestModifyCartRequest(Long id, int quantity, String username) {
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(id);
		request.setQuantity(quantity);
		request.setUsername(username);
		return request;
	}
}

package com.example.demo.model.requests;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class ModifyCartRequestTests {
	
	@Test
    public void testBean() {
    	BeanTestUtils.testSimpleBean(ModifyCartRequest.class);
    }
}

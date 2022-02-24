package com.example.demo.model.persistence;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class CartTests {

	@Test
    public void testBean() {
    	BeanTestUtils.test(Cart.class);
    }

}

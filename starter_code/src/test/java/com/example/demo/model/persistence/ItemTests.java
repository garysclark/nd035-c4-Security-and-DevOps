package com.example.demo.model.persistence;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class ItemTests {

	private static final Long TEST_ID = 0l;
	private static final String TEST_NAME = "Test Item";
	private static final BigDecimal TEST_PRICE = new BigDecimal("9.99");
	private static final String TEST_DESCRIPTION = "Test item description";
	
	@Test
    public void testBean() {
    	BeanTestUtils.test(Item.class);
    }
	
	public static Item getTestItem() {
		Item item = new Item();
		item.setId(TEST_ID);
		item.setName(TEST_NAME);
		item.setDescription(TEST_DESCRIPTION);
		item.setPrice(TEST_PRICE);
		return item;
	}


}

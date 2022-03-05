package com.example.demo.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    	BeanTestUtils.testBean(Item.class);
    }
	
	@Test
	public void canCreateWithAttributes() {
		Item item = getTestItem();
		assertEquals(TEST_ID, item.getId());
		assertEquals(TEST_NAME, item.getName());
		assertEquals(TEST_PRICE, item.getPrice());
		assertEquals(TEST_DESCRIPTION, item.getDescription());
	}
	
	public static Item getTestItem() {
		return new Item(TEST_ID, TEST_NAME, TEST_PRICE, TEST_DESCRIPTION);
	}


}

package com.example.demo.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.BeanTestUtils;

public class CartTests {

	private static final Long TEST_ID = 0l;

	@Test
    public void testBean() {
    	BeanTestUtils.test(Cart.class);
    }

	@Test
	public void canAddAndRemoveItems() {
		Cart cart = new Cart();
		Item item1 = ItemTests.getTestItem();
		Item item2 = ItemTests.getTestItem();
		item2.setId(item2.getId() + 1l);
		item2.setPrice(item1.getPrice().add(BigDecimal.ONE));

		// Add items
		cart.addItem(item1);
		assertEquals(item1, cart.getItems().get(0));
		assertEquals(item1.getPrice(), cart.getTotal());
		cart.addItem(item2);
		assertEquals(item2, cart.getItems().get(1));
		assertEquals(item1.getPrice().add(item2.getPrice()), cart.getTotal());
		assertEquals(2, cart.getItems().size());
		
		// Remove item
		cart.removeItem(item1);
		assertEquals(1, cart.getItems().size());
		assertEquals(item2, cart.getItems().get(0));
		assertEquals(item2.getPrice(), cart.getTotal());
	}
	
	@Test
	public void canHandleItemRemovalFromEmptyCart() {
		Cart cart = new Cart();
		cart.removeItem(ItemTests.getTestItem());
		assertEquals(BigDecimal.ZERO, cart.getTotal());
	}

	public static Cart getTestCart() {
		Cart cart = new Cart();
		cart.setId(TEST_ID);
		cart.addItem(ItemTests.getTestItem());
		return cart;
	}
}

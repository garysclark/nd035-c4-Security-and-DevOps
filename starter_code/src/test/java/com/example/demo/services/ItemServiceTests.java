package com.example.demo.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.ItemTests;
import com.example.demo.model.persistence.repositories.ItemRepository;

@SpringBootTest
public class ItemServiceTests {

	private static final List<Item> TEST_ITEMS = Collections.singletonList(ItemTests.getTestItem());

	private static final Long TEST_INVALID_ID = 99l;

	@MockBean
	private ItemRepository mockItemRepository;

	@Autowired
	private ItemService itemService;
	
	@Test
	public void canAccessService() {
		assertNotNull(itemService);
	}
	
	@Test
	public void canFindAllItems() {
		when(mockItemRepository.findAll()).thenReturn(TEST_ITEMS);
		
		List<Item> items = itemService.findAllItems();
		
		assertNotNull(items);
		assertEquals(TEST_ITEMS, items);
	}
	
	@Test
	public void canFindItemById() {
		Item item = TEST_ITEMS.get(0);
		when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		
		Item foundItem = itemService.findItemById(item.getId());
		
		assertNotNull(foundItem);
		assertEquals(item, foundItem);
	}
	
	@Test
	public void canHandleItemNotFoundById() {
		when(mockItemRepository.findById(TEST_INVALID_ID)).thenReturn(Optional.ofNullable(null));

		assertThrows(EntityNotFoundException.class, ()->{itemService.findItemById(TEST_INVALID_ID);});
	}
	
	@Test
	public void canFindItemsByName() {
		when(mockItemRepository.findByName(TEST_ITEMS.get(0).getName())).thenReturn(TEST_ITEMS);
		
		List<Item> items = itemService.findItemsByName(TEST_ITEMS.get(0).getName());
		
		assertNotNull(items);
		assertEquals(TEST_ITEMS, items);
	}
}


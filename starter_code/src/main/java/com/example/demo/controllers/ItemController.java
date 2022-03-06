package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.services.ItemService;

@RestController
@RequestMapping(ItemController.API_ITEM_ENDPOINT)
public class ItemController {
	
	private static final String GET_ITEM_BY_NAME_ENDPOINT_PART = "/name/{name}";
	private static final String GET_ITEM_BY_ID_ENDPOINT_PART = "/{id}";
	public static final String API_ITEM_ENDPOINT = "/api/item";
	public static final String GET_ALL_ITEMS_ENDPOINT = API_ITEM_ENDPOINT;
	public static final String GET_ITEMS_BY_NAME_ENDPOINT = API_ITEM_ENDPOINT + "/name/";
	public static final String GET_ITEM_BY_ID_ENDPOINT = API_ITEM_ENDPOINT + "/";

	@Autowired
	private ItemService itemService;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		return ResponseEntity.ok(itemService.findAllItems());
	}
	
	@GetMapping(GET_ITEM_BY_ID_ENDPOINT_PART)
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		return ResponseEntity.ok(itemService.findItemById(id));
	}
	
	@GetMapping(GET_ITEM_BY_NAME_ENDPOINT_PART)
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemService.findItemsByName(name);
		return items.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(items);
			
	}
	
}

package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> findAllItems() {
		return itemRepository.findAll();
	}

	public Item findItemById(Long id) {
		Optional<Item> optionalItem = itemRepository.findById(id);
		if(optionalItem.isEmpty()) {
			throw new EntityNotFoundException("Item not found with Id: " + id);
		}
		return optionalItem.get();
	}

	public List<Item> findItemsByName(String name) {
		return itemRepository.findByName(name);
	}

}

package com.example.demo.controller;

import com.example.demo.dto.ItemDTO;
import com.example.demo.model.Item;
import com.example.demo.service.ItemService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // ✅ Missing import added
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * Create a new item.
     * @param item the item to be created
     * @return the saved item with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        logger.info("Received POST request to create item: {}", itemDTO.getItemName());
        Item item = itemService.convertToEntity(itemDTO);
        Item savedItem = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }
    /**
     * Update an existing item by ID.
     * @param id the ID of the item to update
     * @param item the updated item data
     * @return the updated item with HTTP 201 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        Item updatedItem = itemService.updateItem(id, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedItem); // 201
    }

    /**
     * Get all items.
     * @return list of all items
     */
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    /**
     * Get a single item by ID.
     * @param id the ID of the item
     * @return the item if found
     */
    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    /**
     * Delete an item by ID.
     * @param id the ID of the item to delete
     * @return HTTP 202 Accepted status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // 202
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
}
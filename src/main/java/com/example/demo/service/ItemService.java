package com.example.demo.service;

import com.example.demo.dto.ItemDTO;
import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.logging.LogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LogService logService;

    /**
     * Save a new item to the database.
     */
    public Item saveItem(Item item) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logService.info("📦 Created item: " + item.getItemName());
        return itemRepository.save(item);
    }
    

    /**
     * Update an existing item by ID.
     * @param id the ID of the item to update
     * @param newItem the new item data
     * @return the updated item
     */
public Item updateItem(Long id, Item newItem) {
    Item existing = itemRepository.findById(id)
        .orElseThrow(() -> {
            logService.error("❌ Failed to update item: " + id + " → Item not found");
            return new RuntimeException("Item not found");
        });

    // 🔄 Field-by-field update with null checks
    if (newItem.getItemName() != null) existing.setItemName(newItem.getItemName());
    if (newItem.getItemCost() != null) existing.setItemCost(newItem.getItemCost());
    if (newItem.getItemQuantity() != null) existing.setItemQuantity(newItem.getItemQuantity());
    if (newItem.getItemPack() != null) existing.setItemPack(newItem.getItemPack());

    // ⚠️ Warn if itemPack is 'Y' but itemContent is missing
    if ("Y".equals(newItem.getItemPack()) && newItem.getItemContent() == null) {
        logService.warn("⚠️ Item pack is Y but content missing for item ID: " + id);
    }

    existing.setItemContent(newItem.getItemContent());

    if (newItem.getItemDimensions() != null) existing.setItemDimensions(newItem.getItemDimensions());
    if (newItem.getItemOriginLocation() != null) existing.setItemOriginLocation(newItem.getItemOriginLocation());
    if (newItem.getItemShip() != null) existing.setItemShip(newItem.getItemShip());
    if (newItem.getItemCompany() != null) existing.setItemCompany(newItem.getItemCompany());
    if (newItem.getItemManufacturingDateTime() != null) existing.setItemManufacturingDateTime(newItem.getItemManufacturingDateTime());
    if (newItem.getItemExpiryDate() != null) existing.setItemExpiryDate(newItem.getItemExpiryDate());

    logService.info("✏️ Updated item: " + existing.getItemName() + " (ID: " + id + ")");
    return itemRepository.save(existing);
}

    /**
     * Get all items from the database.
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Get a single item by ID.
     */
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    /**
     * Delete an item by ID.
     */
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            logService.warn("⚠️ Tried to delete non-existent item: " + id);
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logService.info("🗑️ User '" + username + "' deleted item with ID: " + id);
            itemRepository.deleteById(id);
        }
    }
    /**
     * Convert DTO to Entity
     */
    public Item convertToEntity(ItemDTO dto) {
        Item item = new Item();
        item.setItemName(dto.getItemName());

        // ✅ Safely parse itemCost from String to Integer
        try {
            int cost = Integer.parseInt(dto.getItemCost());
            item.setItemCost(cost);
        } catch (NumberFormatException e) {
            logService.error("❌ Invalid item cost format for item: " + dto.getItemName());
            throw new IllegalArgumentException("Item cost must be a valid number greater than zero.");
        }

        item.setItemQuantity(dto.getItemQuantity());
        item.setItemPack(dto.getItemPack());
        item.setItemContent(dto.getItemContent());

        if ("Y".equals(dto.getItemPack()) && dto.getItemContent() == null) {
            logService.warn("⚠️ Item pack is Y but content missing for item: " + dto.getItemName());
        }

        item.setItemDimensions(dto.getItemDimensions());
        item.setItemOriginLocation(dto.getItemOriginLocation());
        item.setItemShip(dto.getItemShip());
        item.setItemCompany(dto.getItemCompany());
        item.setItemManufacturingDateTime(dto.getItemManufacturingDateTime());
        item.setItemExpiryDate(dto.getItemExpiryDate());

        return item;
    }
}
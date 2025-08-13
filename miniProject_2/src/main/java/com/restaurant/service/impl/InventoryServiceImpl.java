package com.restaurant.service.impl;

import com.restaurant.entity.InventoryItem;
import com.restaurant.repository.InventoryItemRepository;
import com.restaurant.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryItemRepository inventoryRepository;

    @Override
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Override
    public List<InventoryItem> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems();
    }

    @Override
    public InventoryItem getItemByName(String name) {
        log.debug("Fetching inventory item by name: {}", name);
        return inventoryRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Inventory item not found: {}", name);
                    return new RuntimeException("Inventory item not found: " + name);
                });
    }

    @Override
    public InventoryItem updateItemQuantity(String name, Integer quantity) {
        log.info("Updating inventory item {} quantity to {}", name, quantity);
        InventoryItem item = getItemByName(name);
        item.updateQuantity(quantity);
        InventoryItem savedItem = inventoryRepository.save(item);
        log.info("Successfully updated inventory item {} quantity to {}", name, quantity);
        return savedItem;
    }

    @Override
    public InventoryItem addItemQuantity(String name, Integer quantity) {
        InventoryItem item = getItemByName(name);
        item.addQuantity(quantity);
        return inventoryRepository.save(item);
    }

    @Override
    public InventoryItem reduceItemQuantity(String name, Integer quantity) {
        log.debug("Reducing inventory item {} quantity by {}", name, quantity);
        InventoryItem item = getItemByName(name);
        if (item.getQuantity() < quantity) {
            log.error("Insufficient quantity for {}. Available: {}, Required: {}", name, item.getQuantity(), quantity);
            throw new RuntimeException("Insufficient quantity for " + name + 
                    ". Available: " + item.getQuantity() + ", Required: " + quantity);
        }
        item.reduceQuantity(quantity);
        InventoryItem savedItem = inventoryRepository.save(item);
        log.info("Successfully reduced inventory item {} quantity by {}. New quantity: {}", name, quantity, savedItem.getQuantity());
        return savedItem;
    }

    @Override
    public boolean hasEnoughIngredient(String name, Integer requiredQuantity) {
        Optional<InventoryItem> itemOpt = inventoryRepository.findByName(name);
        if (itemOpt.isEmpty()) {
            return false;
        }
        return itemOpt.get().getQuantity() >= requiredQuantity;
    }

    @Override
    public void reduceIngredientQuantity(String name, Integer quantity) {
        Optional<InventoryItem> itemOpt = inventoryRepository.findByName(name);
        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            item.reduceQuantity(quantity);
            inventoryRepository.save(item);
        }
    }

    @Override
    public void loadCurrentInventoryFromFile() {
        log.info("Loading current inventory from file...");
        try {
            ClassPathResource resource = new ClassPathResource("CurrentInventory.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String line;
            boolean skipFirstLine = true;
            int itemsLoaded = 0;
            
            while ((line = reader.readLine()) != null) {
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue; // Skip date line
                }
                
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String itemName = parts[0].trim();
                    Integer quantity = Integer.parseInt(parts[1].trim());
                    
                    Optional<InventoryItem> existingItem = inventoryRepository.findByName(itemName);
                    if (existingItem.isPresent()) {
                        InventoryItem item = existingItem.get();
                        item.setQuantity(quantity);
                        inventoryRepository.save(item);
                        log.debug("Updated existing inventory item: {} with quantity: {}", itemName, quantity);
                    } else {
                        InventoryItem newItem = new InventoryItem(itemName, quantity, quantity);
                        inventoryRepository.save(newItem);
                        log.debug("Created new inventory item: {} with quantity: {}", itemName, quantity);
                    }
                    itemsLoaded++;
                }
            }
            reader.close();
            log.info("Successfully loaded {} inventory items from file", itemsLoaded);
        } catch (IOException e) {
            log.error("Failed to load current inventory from file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load current inventory from file: " + e.getMessage());
        }
    }

    @Override
    public void restoreInventoryFromFile() {
        log.info("Restoring inventory from initial file...");
        try {
            ClassPathResource resource = new ClassPathResource("InitialInventory.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String line;
            int itemsRestored = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String itemName = parts[0].trim();
                    Integer quantity = Integer.parseInt(parts[1].trim());
                    
                    Optional<InventoryItem> existingItem = inventoryRepository.findByName(itemName);
                    if (existingItem.isPresent()) {
                        InventoryItem item = existingItem.get();
                        item.setQuantity(quantity);
                        item.setInitialQuantity(quantity);
                        inventoryRepository.save(item);
                        log.debug("Restored existing inventory item: {} to quantity: {}", itemName, quantity);
                    } else {
                        InventoryItem newItem = new InventoryItem(itemName, quantity, quantity);
                        inventoryRepository.save(newItem);
                        log.debug("Created new inventory item during restore: {} with quantity: {}", itemName, quantity);
                    }
                    itemsRestored++;
                }
            }
            reader.close();
            log.info("Successfully restored {} inventory items from initial file", itemsRestored);
        } catch (IOException e) {
            log.error("Failed to restore inventory from file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to restore inventory from file: " + e.getMessage());
        }
    }

    @Override
    public long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    @Override
    public long countOutOfStockItems() {
        return inventoryRepository.countOutOfStockItems();
    }

    @Override
    public Long getTotalInventoryQuantity() {
        Long total = inventoryRepository.getTotalInventoryQuantity();
        return total != null ? total : 0L;
    }
}
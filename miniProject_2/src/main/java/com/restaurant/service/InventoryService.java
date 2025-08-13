package com.restaurant.service;

import com.restaurant.entity.InventoryItem;

import java.util.List;

public interface InventoryService {
    
    List<InventoryItem> getAllInventoryItems();
    
    List<InventoryItem> getLowStockItems();
    
    List<InventoryItem> getOutOfStockItems();
    
    InventoryItem getItemByName(String name);
    
    InventoryItem updateItemQuantity(String name, Integer quantity);
    
    InventoryItem addItemQuantity(String name, Integer quantity);
    
    InventoryItem reduceItemQuantity(String name, Integer quantity);
    
    boolean hasEnoughIngredient(String name, Integer requiredQuantity);
    
    void reduceIngredientQuantity(String name, Integer quantity);
    
    void loadCurrentInventoryFromFile();
    
    void restoreInventoryFromFile();
    
    long countLowStockItems();
    
    long countOutOfStockItems();
    
    Long getTotalInventoryQuantity();
}
package org.restaurant.service;

import org.restaurant.entities.MenuItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for menu item operations
 */
public interface MenuItemService {

    /**
     * Create a new menu item
     * @param name Menu item name
     * @param category Menu item category
     * @param price Menu item price
     * @return Created menu item
     */
    MenuItem createMenuItem(String name, String category, BigDecimal price);

    /**
     * Get all menu items
     * @return List of menu items
     */
    List<MenuItem> getAllMenuItems();

    /**
     * Get menu item by ID
     * @param itemId Menu item ID
     * @return Menu item
     */
    MenuItem getMenuItemById(Long itemId);

    /**
     * Update menu item
     * @param itemId Menu item ID
     * @param name New name
     * @param category New category
     * @param price New price
     * @return Updated menu item
     */
    MenuItem updateMenuItem(Long itemId, String name, String category, BigDecimal price);

    /**
     * Delete menu item
     * @param itemId Menu item ID
     */
    void deleteMenuItem(Long itemId);

    /**
     * Get menu items by category
     * @param category Category name
     * @return List of menu items
     */
    List<MenuItem> getMenuItemsByCategory(String category);

    /**
     * Get all categories
     * @return List of categories
     */
    List<String> getAllCategories();

    /**
     * Search menu items by name and/or category
     * @param name Item name (optional)
     * @param category Category (optional)
     * @return List of matching menu items
     */
    List<MenuItem> searchMenuItems(String name, String category);

    /**
     * Get menu items by price range
     * @param min Minimum price
     * @param max Maximum price
     * @return List of menu items
     */
    List<MenuItem> getMenuItemsByPriceRange(BigDecimal min, BigDecimal max);

    /**
     * Get menu items under specified price
     * @param maxPrice Maximum price
     * @return List of menu items
     */
    List<MenuItem> getMenuItemsUnderPrice(BigDecimal maxPrice);

    /**
     * Get menu items by category ordered by price
     * @param category Category name
     * @return List of menu items ordered by price
     */
    List<MenuItem> getMenuItemsByCategoryOrderedByPrice(String category);

    /**
     * Check if menu item exists
     * @param itemId Menu item ID
     * @return true if menu item exists
     */
    boolean menuItemExists(Long itemId);
}
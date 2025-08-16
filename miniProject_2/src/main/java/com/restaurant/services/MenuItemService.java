package org.restaurant.services;

import org.restaurant.entities.MenuItem;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.exceptions.ResourceAlreadyExistsException;
import org.restaurant.repositories.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Create a new menu item
     */
    public MenuItem createMenuItem(String name, String category, BigDecimal price) {
        // Check if menu item with same name already exists
        if (menuItemRepository.existsByMenuItemName(name)) {
            throw new ResourceAlreadyExistsException("Menu item with name '" + name + "' already exists");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setMenuItemName(name);
        menuItem.setCategory(category);
        menuItem.setPrice(price);

        return menuItemRepository.save(menuItem);
    }

    /**
     * Get all menu items
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    /**
     * Get menu item by ID
     */
    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(Long itemId) {
        return menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));
    }

    /**
     * Get menu items by category
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return menuItemRepository.findAllCategories();
    }

    /**
     * Search menu items by name
     */
    @Transactional(readOnly = true)
    public List<MenuItem> searchMenuItemsByName(String name) {
        return menuItemRepository.findByMenuItemNameContainingIgnoreCase(name);
    }

    /**
     * Get menu items by price range
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Get menu items under max price
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsUnderPrice(BigDecimal maxPrice) {
        return menuItemRepository.findByPriceLessThanEqual(maxPrice);
    }

    /**
     * Get menu items above min price
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsAbovePrice(BigDecimal minPrice) {
        return menuItemRepository.findByPriceGreaterThanEqual(minPrice);
    }

    /**
     * Search menu items with filters
     */
    @Transactional(readOnly = true)
    public List<MenuItem> searchMenuItems(String name, String category) {
        return menuItemRepository.searchMenuItems(name, category);
    }

    /**
     * Get menu items by category ordered by price
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByCategoryOrderedByPrice(String category) {
        return menuItemRepository.findByCategoryOrderByPriceAsc(category);
    }

    /**
     * Get most expensive items by category
     */
    @Transactional(readOnly = true)
    public List<MenuItem> getMostExpensiveByCategory(String category) {
        return menuItemRepository.findMostExpensiveByCategory(category);
    }

    /**
     * Update menu item
     */
    public MenuItem updateMenuItem(Long itemId, String name, String category, BigDecimal price) {
        MenuItem existingItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));

        // Check if new name conflicts with existing items (excluding current item)
        if (name != null && !existingItem.getMenuItemName().equals(name)
                && menuItemRepository.existsByMenuItemName(name)) {
            throw new ResourceAlreadyExistsException("Menu item with name '" + name + "' already exists");
        }

        if (name != null) {
            existingItem.setMenuItemName(name);
        }
        if (category != null) {
            existingItem.setCategory(category);
        }
        if (price != null) {
            existingItem.setPrice(price);
        }

        return menuItemRepository.save(existingItem);
    }

    /**
     * Delete menu item
     */
    public void deleteMenuItem(Long itemId) {
        if (!menuItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Menu item not found with ID: " + itemId);
        }
        menuItemRepository.deleteById(itemId);
    }

    /**
     * Check if menu item exists
     */
    @Transactional(readOnly = true)
    public boolean menuItemExists(Long itemId) {
        return menuItemRepository.existsById(itemId);
    }

    /**
     * Check if menu item exists by name
     */
    @Transactional(readOnly = true)
    public boolean menuItemExistsByName(String name) {
        return menuItemRepository.existsByMenuItemName(name);
    }

    /**
     * Get menu item by name
     */
    @Transactional(readOnly = true)
    public MenuItem getMenuItemByName(String name) {
        return menuItemRepository.findByMenuItemName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with name: " + name));
    }
}
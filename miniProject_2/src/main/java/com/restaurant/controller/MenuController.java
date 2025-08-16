package org.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.entities.MenuItem;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuItemService menuItemService;

    /**
     * Create a new menu item
     * POST /menu
     * Required: ADMIN or MANAGER role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        logger.info("Creating new menu item: {} in category: {}", request.getName(), request.getCategory());
        MenuItem menuItem = menuItemService.createMenuItem(request.getName(), request.getCategory(), request.getPrice());
        logger.info("Menu item created successfully with ID: {}", menuItem.getItemId());
        return new ResponseEntity<>(menuItem, HttpStatus.CREATED);
    }

    /**
     * Get all menu items
     * GET /menu
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        logger.info("Fetching all menu items");
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        logger.info("Retrieved {} menu items", menuItems.size());
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu item by ID
     * GET /menu/{id}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        logger.info("Fetching menu item with ID: {}", id);
        MenuItem menuItem = menuItemService.getMenuItemById(id);
        if (menuItem != null) {
            logger.info("Menu item found: {}", menuItem.getMenuItemName());
        } else {
            logger.warn("Menu item with ID: {} not found", id);
        }
        return ResponseEntity.ok(menuItem);
    }

    /**
     * Update menu item
     * PUT /menu/{id}
     * Required: ADMIN or MANAGER role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateMenuItemRequest request) {
        logger.info("Updating menu item with ID: {}", id);
        MenuItem updatedItem = menuItemService.updateMenuItem(id, request.getName(),
                request.getCategory(), request.getPrice());
        logger.info("Menu item updated successfully: {}", updatedItem.getMenuItemName());
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Delete menu item
     * DELETE /menu/{id}
     * Required: ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteMenuItem(@PathVariable Long id) {
        logger.info("Deleting menu item with ID: {}", id);
        menuItemService.deleteMenuItem(id);
        logger.info("Menu item deleted successfully");
        return ResponseEntity.ok(new SuccessResponse("Menu item deleted successfully"));
    }

    /**
     * Get menu items by category
     * GET /menu/category/{category}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable String category) {
        logger.info("Fetching menu items in category: {}", category);
        List<MenuItem> menuItems = menuItemService.getMenuItemsByCategory(category);
        logger.info("Retrieved {} menu items in category: {}", menuItems.size(), category);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get all categories
     * GET /menu/categories
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<String>> getAllCategories() {
        logger.info("Fetching all categories");
        List<String> categories = menuItemService.getAllCategories();
        logger.info("Retrieved {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    /**
     * Search menu items by name
     * GET /menu/search?name={name}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> searchMenuItems(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String category) {
        logger.info("Searching menu items by name: {} and category: {}", name, category);
        List<MenuItem> menuItems;
        if (name != null || category != null) {
            menuItems = menuItemService.searchMenuItems(name, category);
            logger.info("Found {} menu items matching the search criteria", menuItems.size());
        } else {
            menuItems = menuItemService.getAllMenuItems();
            logger.info("No search criteria provided, returning all menu items");
        }
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items by price range
     * GET /menu/price-range?min={min}&max={max}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/price-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> getMenuItemsByPriceRange(@RequestParam BigDecimal min,
                                                                   @RequestParam BigDecimal max) {
        logger.info("Fetching menu items with price between {} and {}", min, max);
        List<MenuItem> menuItems = menuItemService.getMenuItemsByPriceRange(min, max);
        logger.info("Retrieved {} menu items in the price range", menuItems.size());
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items under max price
     * GET /menu/under-price/{maxPrice}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/under-price/{maxPrice}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> getMenuItemsUnderPrice(@PathVariable BigDecimal maxPrice) {
        logger.info("Fetching menu items under the price of {}", maxPrice);
        List<MenuItem> menuItems = menuItemService.getMenuItemsUnderPrice(maxPrice);
        logger.info("Retrieved {} menu items under the specified price", menuItems.size());
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items by category ordered by price
     * GET /menu/category/{category}/ordered
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/category/{category}/ordered")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategoryOrderedByPrice(@PathVariable String category) {
        logger.info("Fetching menu items in category: {} ordered by price", category);
        List<MenuItem> menuItems = menuItemService.getMenuItemsByCategoryOrderedByPrice(category);
        logger.info("Retrieved {} menu items in category: {} ordered by price", menuItems.size(), category);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Check if menu item exists
     * GET /menu/{id}/exists
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<Boolean> menuItemExists(@PathVariable Long id) {
        logger.info("Checking existence of menu item with ID: {}", id);
        boolean exists = menuItemService.menuItemExists(id);
        logger.info("Menu item existence check for ID: {} - {}", id, exists ? "Exists" : "Does not exist");
        return ResponseEntity.ok(exists);
    }

    // Request DTOs as inner classes

    public static class CreateMenuItemRequest {
        @NotBlank(message = "Menu item name is required")
        private String name;

        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static class UpdateMenuItemRequest {
        private String name;
        private String category;
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
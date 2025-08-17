package org.restaurant.repository;

import org.restaurant.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Find by category
    List<MenuItem> findByCategory(String category);

    // Find by name (case insensitive)
    List<MenuItem> findByMenuItemNameContainingIgnoreCase(String name);

    // Find by price range
    List<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find by price less than or equal
    List<MenuItem> findByPriceLessThanEqual(BigDecimal maxPrice);

    // Find by price greater than or equal
    List<MenuItem> findByPriceGreaterThanEqual(BigDecimal minPrice);

    // Check if menu item exists by name
    boolean existsByMenuItemName(String menuItemName);

    // Find by exact name
    Optional<MenuItem> findByMenuItemName(String menuItemName);

    // Get all categories (distinct)
    @Query("SELECT DISTINCT m.category FROM MenuItem m ORDER BY m.category")
    List<String> findAllCategories();

    // Find menu items by category ordered by price
    @Query("SELECT m FROM MenuItem m WHERE m.category = :category ORDER BY m.price ASC")
    List<MenuItem> findByCategoryOrderByPriceAsc(@Param("category") String category);

    // Find most expensive items by category
    @Query("SELECT m FROM MenuItem m WHERE m.category = :category AND m.price = (SELECT MAX(m2.price) FROM MenuItem m2 WHERE m2.category = :category)")
    List<MenuItem> findMostExpensiveByCategory(@Param("category") String category);

    // Search menu items by name and category
    @Query("SELECT m FROM MenuItem m WHERE (:name IS NULL OR LOWER(m.menuItemName) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:category IS NULL OR m.category = :category)")
    List<MenuItem> searchMenuItems(@Param("name") String name, @Param("category") String category);
}
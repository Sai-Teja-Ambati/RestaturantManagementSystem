package com.restaurant.repository;

import com.restaurant.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minThreshold")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity = 0")
    List<InventoryItem> findOutOfStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.name LIKE %:name%")
    List<InventoryItem> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity >= :minQuantity")
    List<InventoryItem> findByQuantityGreaterThanEqual(@Param("minQuantity") Integer minQuantity);
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.quantity <= i.minThreshold")
    long countLowStockItems();
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.quantity = 0")
    long countOutOfStockItems();
    
    @Query("SELECT SUM(i.quantity) FROM InventoryItem i")
    Long getTotalInventoryQuantity();
}
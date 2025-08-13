package com.restaurant.repository;

import com.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    
    Optional<RestaurantTable> findByTableNumber(Integer tableNumber);
    
    List<RestaurantTable> findByIsOccupied(Boolean isOccupied);
    
    List<RestaurantTable> findByIsServed(Boolean isServed);
    
    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);
    
    @Query("SELECT t FROM RestaurantTable t WHERE t.isOccupied = false AND t.capacity >= :requiredCapacity")
    List<RestaurantTable> findAvailableTablesByCapacity(@Param("requiredCapacity") Integer requiredCapacity);
    
    @Query("SELECT t FROM RestaurantTable t WHERE t.isOccupied = false")
    List<RestaurantTable> findAvailableTables();
    
    @Query("SELECT t FROM RestaurantTable t WHERE t.isOccupied = true AND t.isServed = false")
    List<RestaurantTable> findOccupiedUnservedTables();
    
    @Query("SELECT t FROM RestaurantTable t WHERE t.id NOT IN " +
           "(SELECT tr.table.id FROM TableReservation tr WHERE tr.status = 'ACTIVE' " +
           "AND ((tr.startTime <= :endTime AND tr.endTime >= :startTime)))")
    List<RestaurantTable> findAvailableTablesForTimeSlot(@Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.isOccupied = true")
    long countOccupiedTables();
    
    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.isOccupied = false")
    long countAvailableTables();
}
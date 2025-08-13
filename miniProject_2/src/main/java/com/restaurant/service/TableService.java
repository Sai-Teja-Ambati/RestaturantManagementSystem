package com.restaurant.service;

import com.restaurant.entity.RestaurantTable;

import java.time.LocalDateTime;
import java.util.List;

public interface TableService {
    
    List<RestaurantTable> getAllTables();
    
    List<RestaurantTable> getAvailableTables();
    
    List<RestaurantTable> getOccupiedTables();
    
    List<RestaurantTable> getAvailableTablesByCapacity(Integer capacity);
    
    RestaurantTable getTableByNumber(Integer tableNumber);
    
    RestaurantTable occupyTable(Integer tableNumber);
    
    RestaurantTable serveTable(Integer tableNumber);
    
    RestaurantTable freeTable(Integer tableNumber);
    
    RestaurantTable addTable(Integer tableNumber, Integer capacity);
    
    List<RestaurantTable> getAvailableTablesForTimeSlot(LocalDateTime startTime, LocalDateTime endTime);
    
    boolean isTableAvailable(Integer tableNumber);
    
    long countOccupiedTables();
    
    long countAvailableTables();
}
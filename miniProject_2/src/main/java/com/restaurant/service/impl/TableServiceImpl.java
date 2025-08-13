package com.restaurant.service.impl;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.repository.RestaurantTableRepository;
import com.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TableServiceImpl implements TableService {

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Override
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public List<RestaurantTable> getAvailableTables() {
        return tableRepository.findAvailableTables();
    }

    @Override
    public List<RestaurantTable> getOccupiedTables() {
        return tableRepository.findByIsOccupied(true);
    }

    @Override
    public List<RestaurantTable> getAvailableTablesByCapacity(Integer capacity) {
        return tableRepository.findAvailableTablesByCapacity(capacity);
    }

    @Override
    public RestaurantTable getTableByNumber(Integer tableNumber) {
        return tableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Table not found: " + tableNumber));
    }

    @Override
    public RestaurantTable occupyTable(Integer tableNumber) {
        RestaurantTable table = getTableByNumber(tableNumber);
        
        if (table.getIsOccupied()) {
            throw new RuntimeException("Table " + tableNumber + " is already occupied");
        }
        
        table.setIsOccupied(true);
        table.setBookingStartTime(LocalDateTime.now());
        return tableRepository.save(table);
    }

    @Override
    public RestaurantTable serveTable(Integer tableNumber) {
        RestaurantTable table = getTableByNumber(tableNumber);
        
        if (!table.getIsOccupied()) {
            throw new RuntimeException("Table " + tableNumber + " is not occupied");
        }
        
        table.setIsServed(true);
        return tableRepository.save(table);
    }

    @Override
    public RestaurantTable freeTable(Integer tableNumber) {
        RestaurantTable table = getTableByNumber(tableNumber);
        
        table.setIsOccupied(false);
        table.setIsServed(false);
        table.setBookingStartTime(null);
        table.setBookingEndTime(null);
        
        return tableRepository.save(table);
    }

    @Override
    public RestaurantTable addTable(Integer tableNumber, Integer capacity) {
        if (tableRepository.findByTableNumber(tableNumber).isPresent()) {
            throw new RuntimeException("Table " + tableNumber + " already exists");
        }
        
        RestaurantTable table = new RestaurantTable(tableNumber, capacity);
        return tableRepository.save(table);
    }

    @Override
    public List<RestaurantTable> getAvailableTablesForTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        return tableRepository.findAvailableTablesForTimeSlot(startTime, endTime);
    }

    @Override
    public boolean isTableAvailable(Integer tableNumber) {
        RestaurantTable table = getTableByNumber(tableNumber);
        return !table.getIsOccupied();
    }

    @Override
    public long countOccupiedTables() {
        return tableRepository.countOccupiedTables();
    }

    @Override
    public long countAvailableTables() {
        return tableRepository.countAvailableTables();
    }
}
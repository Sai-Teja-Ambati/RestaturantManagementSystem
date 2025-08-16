package org.restaurant.service.impl;

import org.restaurant.entities.RestaurantTable;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.repository.RestaurantTableRepository;
import org.restaurant.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RestaurantTableServiceImpl implements RestaurantTableService {

    @Autowired
    private RestaurantTableRepository tableRepository;

    /**
     * Create a new table
     */
    public RestaurantTable createTable(Integer capacity) {
        if (capacity < 1) {
            throw new BusinessLogicException("Table capacity must be at least 1");
        }

        RestaurantTable table = new RestaurantTable();
        table.setCapacity(capacity);
        table.setStatus(TableStatus.AVAILABLE);

        return tableRepository.save(table);
    }

    /**
     * Get all tables
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    /**
     * Get table by ID
     */
    @Transactional(readOnly = true)
    public RestaurantTable getTableById(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));
    }

    /**
     * Get tables by status
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getTablesByStatus(TableStatus status) {
        return tableRepository.findByStatus(status);
    }

    /**
     * Get available tables
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getAvailableTables() {
        return tableRepository.findByStatus(TableStatus.AVAILABLE);
    }

    /**
     * Get occupied tables
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getOccupiedTables() {
        return tableRepository.findByStatus(TableStatus.OCCUPIED);
    }

    /**
     * Get tables by capacity
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacity(capacity);
    }

    /**
     * Get tables with minimum capacity
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getTablesWithMinCapacity(Integer minCapacity) {
        return tableRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    /**
     * Get available tables with minimum capacity
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getAvailableTablesWithMinCapacity(Integer minCapacity) {
        return tableRepository.findAvailableTablesWithMinCapacity(TableStatus.AVAILABLE, minCapacity);
    }

    /**
     * Find best available table for given capacity
     */
    @Transactional(readOnly = true)
    public RestaurantTable findBestAvailableTable(Integer requiredCapacity) {
        return tableRepository.findSmallestAvailableTable(requiredCapacity)
                .orElseThrow(() -> new ResourceNotFoundException("No available table found for capacity: " + requiredCapacity));
    }

    /**
     * Update table capacity
     */
    public RestaurantTable updateTableCapacity(Long tableId, Integer newCapacity) {
        // Always find the table first, even for validation
        RestaurantTable table = getTableById(tableId);  // This calls findById()

        if (newCapacity <= 0) {
            throw new BusinessLogicException("Table capacity must be at least 1");
        }

        table.setCapacity(newCapacity);
        return tableRepository.save(table);
    }


    /**
     * Update table status
     */
    public RestaurantTable updateTableStatus(Long tableId, TableStatus status) {
        RestaurantTable table = getTableById(tableId);
        table.setStatus(status);
        return tableRepository.save(table);
    }

    /**
     * Mark table as occupied
     */
    public RestaurantTable markTableAsOccupied(Long tableId) {
        RestaurantTable table = getTableById(tableId);

        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new BusinessLogicException("Table is already occupied");
        }

        if (table.getStatus() == TableStatus.RESERVED) {
            throw new BusinessLogicException("Table is reserved");
        }

        table.setStatus(TableStatus.OCCUPIED);
        return tableRepository.save(table);
    }
    /**
     * Mark table as reserved
     */
    public RestaurantTable markTableAsReserved(Long tableId) {
        RestaurantTable table = getTableById(tableId);

        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new BusinessLogicException("Cannot reserve an occupied table");
        }

        table.setStatus(TableStatus.RESERVED);
        return tableRepository.save(table);
    }

    /**
     * Get reserved tables
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getReservedTables() {
        return tableRepository.findByStatus(TableStatus.RESERVED);
    }


    /**
     * Mark table as available
     */
    public RestaurantTable markTableAsAvailable(Long tableId) {
        RestaurantTable table = getTableById(tableId);
        table.setStatus(TableStatus.AVAILABLE);
        return tableRepository.save(table);
    }

    /**
     * Delete table
     */
    public void deleteTable(Long tableId) {
        RestaurantTable table = getTableById(tableId);

        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new BusinessLogicException("Cannot delete occupied table");
        }

        tableRepository.deleteById(tableId);
    }

    /**
     * Get table statistics
     */
    @Transactional(readOnly = true)
    public RestaurantTableService.TableStatistics getTableStatistics() {
        long totalTables = tableRepository.count();
        long availableTables = tableRepository.countByStatus(TableStatus.AVAILABLE);
        long occupiedTables = tableRepository.countByStatus(TableStatus.OCCUPIED);
        long reservedTables = tableRepository.countByStatus(TableStatus.RESERVED);

        return new RestaurantTableService.TableStatistics((int)totalTables, (int)availableTables, (int)reservedTables, (int)occupiedTables);
    }

    /**
     * Get tables by capacity range
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        return tableRepository.findByCapacityRangeAndStatus(minCapacity, maxCapacity, TableStatus.AVAILABLE);
    }

    /**
     * Check if table exists
     */
    @Transactional(readOnly = true)
    public boolean tableExists(Long tableId) {
        return tableRepository.existsById(tableId);
    }

    /**
     * Get all available tables ordered by capacity
     */
    @Transactional(readOnly = true)
    public List<RestaurantTable> getAllAvailableTablesOrderedByCapacity() {
        return tableRepository.findAllAvailableTablesOrderByCapacity();
    }


}
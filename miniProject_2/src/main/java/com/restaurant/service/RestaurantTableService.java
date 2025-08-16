package org.restaurant.service;

import org.restaurant.entities.RestaurantTable;
import org.restaurant.enums.TableStatus;

import java.util.List;

/**
 * Service interface for restaurant table operations
 */
public interface RestaurantTableService {

    /**
     * Create a new table
     * @param capacity Table capacity
     * @return Created table
     */
    RestaurantTable createTable(Integer capacity);

    /**
     * Get all tables
     * @return List of tables
     */
    List<RestaurantTable> getAllTables();

    /**
     * Get table by ID
     * @param tableId Table ID
     * @return Restaurant table
     */
    RestaurantTable getTableById(Long tableId);

    /**
     * Update table capacity
     * @param tableId Table ID
     * @param capacity New capacity
     * @return Updated table
     */
    RestaurantTable updateTableCapacity(Long tableId, Integer capacity);

    /**
     * Update table status
     * @param tableId Table ID
     * @param status New status
     * @return Updated table
     */
    RestaurantTable updateTableStatus(Long tableId, TableStatus status);

    /**
     * Mark table as reserved
     * @param tableId Table ID
     * @return Updated table
     */
    RestaurantTable markTableAsReserved(Long tableId);

    /**
     * Mark table as occupied
     * @param tableId Table ID
     * @return Updated table
     */
    RestaurantTable markTableAsOccupied(Long tableId);

    /**
     * Mark table as available
     * @param tableId Table ID
     * @return Updated table
     */
    RestaurantTable markTableAsAvailable(Long tableId);

    /**
     * Delete table
     * @param tableId Table ID
     */
    void deleteTable(Long tableId);

    /**
     * Get tables by status
     * @param status Table status
     * @return List of tables
     */
    List<RestaurantTable> getTablesByStatus(TableStatus status);

    /**
     * Get available tables
     * @return List of available tables
     */
    List<RestaurantTable> getAvailableTables();

    /**
     * Get reserved tables
     * @return List of reserved tables
     */
    List<RestaurantTable> getReservedTables();

    /**
     * Get occupied tables
     * @return List of occupied tables
     */
    List<RestaurantTable> getOccupiedTables();

    /**
     * Get tables by capacity
     * @param capacity Table capacity
     * @return List of tables
     */
    List<RestaurantTable> getTablesByCapacity(Integer capacity);

    /**
     * Get tables with minimum capacity
     * @param minCapacity Minimum capacity
     * @return List of tables
     */
    List<RestaurantTable> getTablesWithMinCapacity(Integer minCapacity);

    /**
     * Get available tables with minimum capacity
     * @param minCapacity Minimum capacity
     * @return List of available tables
     */
    List<RestaurantTable> getAvailableTablesWithMinCapacity(Integer minCapacity);

    /**
     * Find best available table for given capacity
     * @param requiredCapacity Required capacity
     * @return Best available table
     */
    RestaurantTable findBestAvailableTable(Integer requiredCapacity);

    /**
     * Get table statistics
     * @return Table statistics
     */
    TableStatistics getTableStatistics();

    /**
     * Get tables by capacity range
     * @param min Minimum capacity
     * @param max Maximum capacity
     * @return List of tables
     */
    List<RestaurantTable> getTablesByCapacityRange(Integer min, Integer max);

    /**
     * Get all available tables ordered by capacity
     * @return List of available tables ordered by capacity
     */
    List<RestaurantTable> getAllAvailableTablesOrderedByCapacity();

    /**
     * Table statistics inner class
     */
    class TableStatistics {
        private int totalTables;
        private int availableTables;
        private int reservedTables;
        private int occupiedTables;

        // Constructors, getters, and setters
        public TableStatistics() {}

        public TableStatistics(int totalTables, int availableTables, int reservedTables, int occupiedTables) {
            this.totalTables = totalTables;
            this.availableTables = availableTables;
            this.reservedTables = reservedTables;
            this.occupiedTables = occupiedTables;
        }

        public int getTotalTables() { return totalTables; }
        public void setTotalTables(int totalTables) { this.totalTables = totalTables; }
        public int getAvailableTables() { return availableTables; }
        public void setAvailableTables(int availableTables) { this.availableTables = availableTables; }
        public int getReservedTables() { return reservedTables; }
        public void setReservedTables(int reservedTables) { this.reservedTables = reservedTables; }
        public int getOccupiedTables() { return occupiedTables; }
        public void setOccupiedTables(int occupiedTables) { this.occupiedTables = occupiedTables; }
    }
}
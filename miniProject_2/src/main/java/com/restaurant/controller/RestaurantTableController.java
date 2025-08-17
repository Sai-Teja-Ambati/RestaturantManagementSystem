package org.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.TableStatus;
import org.restaurant.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/tables")
@CrossOrigin(origins = "*")
public class RestaurantTableController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantTableController.class);

    @Autowired
    private RestaurantTableService tableService;

    /**
     * Create a new table
     * POST /tables
     * Required: ADMIN or MANAGER role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RestaurantTable> createTable(@Valid @RequestBody CreateTableRequest request) {
        logger.info("Creating new table with capacity: {}", request.getCapacity());
        RestaurantTable table = tableService.createTable(request.getCapacity());
        logger.info("Table created successfully with ID: {}", table.getTableId());
        return new ResponseEntity<>(table, HttpStatus.CREATED);
    }

    /**
     * Get all tables
     * GET /tables
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        logger.info("Fetching all tables");
        List<RestaurantTable> tables = tableService.getAllTables();
        logger.info("Retrieved {} tables", tables.size());
        return ResponseEntity.ok(tables);
    }

    /**
     * Get table by ID
     * GET /tables/{id}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        logger.info("Fetching table with ID: {}", id);
        RestaurantTable table = tableService.getTableById(id);
        logger.info("Table details: {}", table);
        return ResponseEntity.ok(table);
    }

    /**
     * Update table capacity
     * PUT /tables/{id}/capacity
     * Required: ADMIN or MANAGER role
     */
    @PutMapping("/{id}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RestaurantTable> updateTableCapacity(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateCapacityRequest request) {
        logger.info("Updating capacity of table ID {} to {}", id, request.getCapacity());
        RestaurantTable table = tableService.updateTableCapacity(id, request.getCapacity());
        logger.info("Table ID {} capacity updated to {}", id, request.getCapacity());
        return ResponseEntity.ok(table);
    }

    /**
     * Update table status
     * PUT /tables/{id}/status
     * Required: ADMIN or MANAGER role
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RestaurantTable> updateTableStatus(@PathVariable Long id,
                                                             @Valid @RequestBody UpdateStatusRequest request) {
        logger.info("Updating status of table ID {} to {}", id, request.getStatus());
        RestaurantTable table = tableService.updateTableStatus(id, request.getStatus());
        logger.info("Table ID {} status updated to {}", id, request.getStatus());
        return ResponseEntity.ok(table);
    }

    /**
     * Mark table as reserved
     * PUT /tables/{id}/reserve
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<RestaurantTable> markTableAsReserved(@PathVariable Long id) {
        logger.info("Marking table ID {} as reserved", id);
        RestaurantTable table = tableService.markTableAsReserved(id);
        logger.info("Table ID {} marked as reserved", id);
        return ResponseEntity.ok(table);
    }

    /**
     * Get reserved tables
     * GET /tables/reserved
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/reserved")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getReservedTables() {
        logger.info("Fetching reserved tables");
        List<RestaurantTable> tables = tableService.getReservedTables();
        logger.info("Retrieved {} reserved tables", tables.size());
        return ResponseEntity.ok(tables);
    }

    /**
     * Delete table
     * DELETE /tables/{id}
     * Required: ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteTable(@PathVariable Long id) {
        logger.info("Deleting table with ID: {}", id);
        tableService.deleteTable(id);
        logger.info("Table with ID: {} deleted successfully", id);
        return ResponseEntity.ok(new SuccessResponse("Table deleted successfully"));
    }

    /**
     * Get tables by status
     * GET /tables/status/{status}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getTablesByStatus(@PathVariable TableStatus status) {
        logger.info("Fetching tables with status: {}", status);
        List<RestaurantTable> tables = tableService.getTablesByStatus(status);
        logger.info("Retrieved {} tables with status {}", tables.size(), status);
        return ResponseEntity.ok(tables);
    }

    /**
     * Get available tables
     * GET /tables/available
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables() {
        logger.info("Fetching available tables");
        List<RestaurantTable> tables = tableService.getAvailableTables();
        logger.info("Retrieved {} available tables", tables.size());
        return ResponseEntity.ok(tables);
    }

    /**
     * Get occupied tables
     * GET /tables/occupied
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/occupied")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getOccupiedTables() {
        logger.info("Fetching occupied tables");
        List<RestaurantTable> tables = tableService.getOccupiedTables();
        logger.info("Retrieved {} occupied tables", tables.size());
        return ResponseEntity.ok(tables);
    }

    /**
     * Get tables by capacity
     * GET /tables/capacity/{capacity}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/capacity/{capacity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getTablesByCapacity(@PathVariable Integer capacity) {
        logger.info("Fetching tables with capacity: {}", capacity);
        List<RestaurantTable> tables = tableService.getTablesByCapacity(capacity);
        logger.info("Retrieved {} tables with capacity {}", tables.size(), capacity);
        return ResponseEntity.ok(tables);
    }

    /**
     * Get tables with minimum capacity
     * GET /tables/min-capacity/{minCapacity}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/min-capacity/{minCapacity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getTablesWithMinCapacity(@PathVariable Integer minCapacity) {
        logger.info("Fetching tables with minimum capacity: {}", minCapacity);
        List<RestaurantTable> tables = tableService.getTablesWithMinCapacity(minCapacity);
        logger.info("Retrieved {} tables with minimum capacity {}", tables.size(), minCapacity);
        return ResponseEntity.ok(tables);
    }

    /**
     * Get available tables with minimum capacity
     * GET /tables/available/min-capacity/{minCapacity}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/available/min-capacity/{minCapacity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getAvailableTablesWithMinCapacity(@PathVariable Integer minCapacity) {
        logger.info("Fetching available tables with minimum capacity: {}", minCapacity);
        List<RestaurantTable> tables = tableService.getAvailableTablesWithMinCapacity(minCapacity);
        logger.info("Retrieved {} available tables with minimum capacity {}", tables.size(), minCapacity);
        return ResponseEntity.ok(tables);
    }

    /**
     * Find best available table for given capacity
     * GET /tables/best-available/{requiredCapacity}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/best-available/{requiredCapacity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<RestaurantTable> findBestAvailableTable(@PathVariable Integer requiredCapacity) {
        logger.info("Finding best available table for required capacity: {}", requiredCapacity);
        RestaurantTable table = tableService.findBestAvailableTable(requiredCapacity);
        logger.info("Best available table found: {}", table);
        return ResponseEntity.ok(table);
    }

    /**
     * Mark table as occupied
     * PUT /tables/{id}/occupy
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}/occupy")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<RestaurantTable> markTableAsOccupied(@PathVariable Long id) {
        logger.info("Marking table ID {} as occupied", id);
        RestaurantTable table = tableService.markTableAsOccupied(id);
        logger.info("Table ID {} marked as occupied", id);
        return ResponseEntity.ok(table);
    }

    /**
     * Mark table as available
     * PUT /tables/{id}/free
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}/free")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<RestaurantTable> markTableAsAvailable(@PathVariable Long id) {
        logger.info("Marking table ID {} as available", id);
        RestaurantTable table = tableService.markTableAsAvailable(id);
        logger.info("Table ID {} marked as available", id);
        return ResponseEntity.ok(table);
    }

    /**
     * Get table statistics
     * GET /tables/statistics
     * Required: ADMIN or MANAGER role
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RestaurantTableService.TableStatistics> getTableStatistics() {
        logger.info("Fetching table statistics");
        RestaurantTableService.TableStatistics stats = tableService.getTableStatistics();
        logger.info("Table statistics: {}", stats);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get tables by capacity range
     * GET /tables/capacity-range?min={min}&max={max}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/capacity-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getTablesByCapacityRange(@RequestParam Integer min,
                                                                          @RequestParam Integer max) {
        logger.info("Fetching tables by capacity range: min={}, max={}", min, max);
        List<RestaurantTable> tables = tableService.getTablesByCapacityRange(min, max);
        logger.info("Retrieved {} tables in capacity range {} - {}", tables.size(), min, max);
        return ResponseEntity.ok(tables);
    }

    /**
     * Get all available tables ordered by capacity
     * GET /tables/available/ordered
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/available/ordered")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<RestaurantTable>> getAllAvailableTablesOrderedByCapacity() {
        logger.info("Fetching all available tables ordered by capacity");
        List<RestaurantTable> tables = tableService.getAllAvailableTablesOrderedByCapacity();
        logger.info("Retrieved {} available tables ordered by capacity", tables.size());
        return ResponseEntity.ok(tables);
    }

    // Request DTOs as inner classes

    public static class CreateTableRequest {
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        private Integer capacity;

        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
    }

    public static class UpdateCapacityRequest {
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        private Integer capacity;

        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
    }

    public static class UpdateStatusRequest {
        @NotNull(message = "Status is required")
        private TableStatus status;

        public TableStatus getStatus() { return status; }
        public void setStatus(TableStatus status) { this.status = status; }
    }
}
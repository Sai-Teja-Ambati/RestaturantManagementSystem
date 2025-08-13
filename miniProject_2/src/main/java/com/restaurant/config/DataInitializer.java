package com.restaurant.config;

import com.restaurant.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Users and tables are now initialized by Flyway migrations
        initializeInventory();
        
        log.info("Data initialization completed successfully!");
    }

    private void initializeInventory() {
        try {
            log.info("Initializing inventory data...");
            // Load inventory from files if database is empty
            if (inventoryService.getAllInventoryItems().isEmpty()) {
                log.info("Database inventory is empty, loading from file...");
                inventoryService.loadCurrentInventoryFromFile();
                log.info("Inventory loaded successfully from file");
            } else {
                log.info("Inventory already exists in database, skipping file load");
            }
        } catch (Exception e) {
            log.error("Failed to initialize inventory: {}", e.getMessage(), e);
        }
    }
}
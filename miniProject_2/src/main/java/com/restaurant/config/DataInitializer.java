package com.restaurant.config;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.User;
import com.restaurant.repository.RestaurantTableRepository;
import com.restaurant.service.InventoryService;
import com.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeTables();
        initializeInventory();
    }

    private void initializeUsers() {
        // Create admin user
        if (!userService.existsByEmail("admin@restaurant.com")) {
            userService.createUser("admin", "admin@restaurant.com", "admin123", User.Role.ADMIN);
        }

        // Create waiter users
        for (int i = 1; i <= 5; i++) {
            String email = "waiter" + i + "@restaurant.com";
            if (!userService.existsByEmail(email)) {
                userService.createUser("waiter" + i, email, "waiter123", User.Role.WAITER);
            }
        }

        // Create sample customer
        if (!userService.existsByEmail("customer1@example.com")) {
            userService.createUser("customer1", "customer1@example.com", "customer123", User.Role.CUSTOMER);
        }
    }

    private void initializeTables() {
        if (tableRepository.count() == 0) {
            // Table 1: Small table for two
            tableRepository.save(new RestaurantTable(1, 2));
            
            // Table 2-3, 5, 8-9, 12: Standard 4-person tables
            tableRepository.save(new RestaurantTable(2, 4));
            tableRepository.save(new RestaurantTable(3, 4));
            tableRepository.save(new RestaurantTable(5, 4));
            tableRepository.save(new RestaurantTable(8, 4));
            tableRepository.save(new RestaurantTable(9, 4));
            tableRepository.save(new RestaurantTable(12, 4));
            
            // Table 4, 10: Larger 6-person tables
            tableRepository.save(new RestaurantTable(4, 6));
            tableRepository.save(new RestaurantTable(10, 6));
            
            // Table 6: Large group table
            tableRepository.save(new RestaurantTable(6, 8));
            
            // Table 7: Small table for two
            tableRepository.save(new RestaurantTable(7, 2));
            
            // Table 11: Very large group table
            tableRepository.save(new RestaurantTable(11, 10));
        }
    }

    private void initializeInventory() {
        try {
            // Load inventory from files if database is empty
            if (inventoryService.getAllInventoryItems().isEmpty()) {
                inventoryService.loadCurrentInventoryFromFile();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize inventory: " + e.getMessage());
        }
    }
}
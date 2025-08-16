package org.restaurant.config;

import org.restaurant.entities.MenuItem;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.entities.User;
import org.restaurant.enums.TableStatus;
import org.restaurant.enums.UserRole;
import org.restaurant.repositories.MenuItemRepository;
import org.restaurant.repositories.RestaurantTableRepository;
import org.restaurant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${restaurant.default.admin.username:admin}")
    private String adminUsername;

    @Value("${restaurant.default.admin.password:admin123}")
    private String adminPassword;

    @Value("${restaurant.default.admin.email:admin@restaurant.com}")
    private String adminEmail;

    @Value("${restaurant.default.admin.name:System Administrator}")
    private String adminName;

    @Value("${restaurant.table.min-capacity:1}")
    private int minTableCapacity;

    @Value("${restaurant.table.max-capacity:12}")
    private int maxTableCapacity;

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize default users
        initializeDefaultUsers();

        // Initialize default tables
        initializeDefaultTables();

        // Initialize default menu items
        initializeDefaultMenuItems();

        System.out.println("Data initialization completed successfully.");
    }

    private void initializeDefaultUsers() {
        // Check and create default users
        createDefaultUserIfNotExists(UserRole.ADMIN, adminUsername, adminPassword, adminEmail, adminName);
        createDefaultUserIfNotExists(UserRole.MANAGER, "manager", "manager123", "manager@restaurant.com", "Restaurant Manager");
        createDefaultUserIfNotExists(UserRole.CHEF, "chef", "chef123", "chef@restaurant.com", "Head Chef");
        createDefaultUserIfNotExists(UserRole.WAITER, "waiter", "waiter123", "waiter@restaurant.com", "Head Waiter");

        System.out.println("Default users have been initialized successfully.");
    }

    private void initializeDefaultTables() {
        // Check if tables exist
        long tableCount = tableRepository.count();
        if (tableCount == 0) {
            // Create tables with different capacities
            List<Integer> tableCapacities = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 8, 10);
            for (int i = 0; i < tableCapacities.size(); i++) {
                RestaurantTable table = new RestaurantTable();
                table.setCapacity(tableCapacities.get(i));
                table.setStatus(TableStatus.AVAILABLE);
                tableRepository.save(table);
            }
            System.out.println("Created " + tableCapacities.size() + " default tables.");
        } else {
            System.out.println("Tables already exist in the database. Skipping table initialization.");
        }
    }

    private void initializeDefaultMenuItems() {
        // Check if menu items exist
        long menuItemCount = menuItemRepository.count();
        if (menuItemCount == 0) {
            // Create menu items by category

            // Appetizers
            createMenuItem("Mozzarella Sticks", "Appetizers", new BigDecimal("7.99"));
            createMenuItem("Bruschetta", "Appetizers", new BigDecimal("6.99"));
            createMenuItem("Chicken Wings", "Appetizers", new BigDecimal("9.99"));
            createMenuItem("Loaded Nachos", "Appetizers", new BigDecimal("8.99"));

            // Main Courses
            createMenuItem("Spaghetti Bolognese", "Main Courses", new BigDecimal("14.99"));
            createMenuItem("Grilled Salmon", "Main Courses", new BigDecimal("18.99"));
            createMenuItem("Chicken Parmesan", "Main Courses", new BigDecimal("16.99"));
            createMenuItem("Beef Burger", "Main Courses", new BigDecimal("13.99"));
            createMenuItem("Vegetable Stir Fry", "Main Courses", new BigDecimal("12.99"));

            // Pizzas
            createMenuItem("Margherita Pizza", "Pizzas", new BigDecimal("11.99"));
            createMenuItem("Pepperoni Pizza", "Pizzas", new BigDecimal("13.99"));
            createMenuItem("Vegetarian Pizza", "Pizzas", new BigDecimal("12.99"));

            // Desserts
            createMenuItem("Tiramisu", "Desserts", new BigDecimal("6.99"));
            createMenuItem("Chocolate Cake", "Desserts", new BigDecimal("5.99"));
            createMenuItem("Ice Cream", "Desserts", new BigDecimal("4.99"));

            // Beverages
            createMenuItem("Soft Drink", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Iced Tea", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Coffee", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Water", "Beverages", new BigDecimal("1.99"));

            System.out.println("Created default menu items across multiple categories.");
        } else {
            System.out.println("Menu items already exist in the database. Skipping menu initialization.");
        }
    }

    private void createMenuItem(String name, String category, BigDecimal price) {
        MenuItem item = new MenuItem();
        item.setMenuItemName(name);
        item.setCategory(category);
        item.setPrice(price);
        menuItemRepository.save(item);
    }

    private void createDefaultUserIfNotExists(UserRole role, String username, String password, String email, String fullName) {
        if (!userRepository.existsByRole(role)) {
            // If no user exists with this role, create one
            if (!userRepository.existsByUsername(username)) {
                User user = new User();
                user.setFullName(fullName);
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(role);
                userRepository.save(user);
                System.out.println("Created default " + role + " user: " + username);
            } else {
                // If username exists but with different role, create with numbered username
                int counter = 1;
                String newUsername = username + counter;
                while (userRepository.existsByUsername(newUsername)) {
                    counter++;
                    newUsername = username + counter;
                }
                User user = new User();
                user.setFullName(fullName);
                user.setEmail(counter + "." + email); // Modify email to avoid uniqueness conflicts
                user.setUsername(newUsername);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(role);
                userRepository.save(user);
                System.out.println("Created default " + role + " user: " + newUsername);
            }
        } else {
            System.out.println("A " + role + " user already exists.");
        }
    }
}

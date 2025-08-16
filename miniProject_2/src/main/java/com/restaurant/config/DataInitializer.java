package org.restaurant.config;

import org.restaurant.entities.MenuItem;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.entities.User;
import org.restaurant.enums.TableStatus;
import org.restaurant.enums.UserRole;
import org.restaurant.repository.MenuItemRepository;
import org.restaurant.repository.RestaurantTableRepository;
import org.restaurant.repository.UserRepository;
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
            // Create comprehensive Indian menu items by category (prices in USD)

            // Veg Starters
            createMenuItem("Paneer Tikka", "Veg Starters", new BigDecimal("6.99"));
            createMenuItem("Veg Spring Rolls", "Veg Starters", new BigDecimal("5.99"));
            createMenuItem("Gobi Manchurian", "Veg Starters", new BigDecimal("5.49"));
            createMenuItem("Hara Bhara Kebab", "Veg Starters", new BigDecimal("6.49"));
            createMenuItem("Aloo Tikki", "Veg Starters", new BigDecimal("4.99"));
            createMenuItem("Crispy Corn", "Veg Starters", new BigDecimal("5.49"));
            createMenuItem("Veg Seekh Kebab", "Veg Starters", new BigDecimal("6.49"));
            createMenuItem("Samosa Chaat", "Veg Starters", new BigDecimal("4.49"));

            // Non-Veg Starters
            createMenuItem("Chicken Tikka", "Non-Veg Starters", new BigDecimal("7.99"));
            createMenuItem("Fish Amritsari", "Non-Veg Starters", new BigDecimal("8.99"));
            createMenuItem("Mutton Seekh Kebab", "Non-Veg Starters", new BigDecimal("9.49"));
            createMenuItem("Chicken 65", "Non-Veg Starters", new BigDecimal("7.49"));
            createMenuItem("Tandoori Prawns", "Non-Veg Starters", new BigDecimal("10.00"));
            createMenuItem("Chilli Chicken", "Non-Veg Starters", new BigDecimal("7.99"));
            createMenuItem("Drums of Heaven", "Non-Veg Starters", new BigDecimal("8.49"));
            createMenuItem("Tandoori Wings", "Non-Veg Starters", new BigDecimal("7.49"));

            // Indian Breads
            createMenuItem("Naan", "Indian Breads", new BigDecimal("2.99"));
            createMenuItem("Butter Naan", "Indian Breads", new BigDecimal("3.49"));
            createMenuItem("Garlic Naan", "Indian Breads", new BigDecimal("3.99"));
            createMenuItem("Roti", "Indian Breads", new BigDecimal("1.99"));
            createMenuItem("Butter Roti", "Indian Breads", new BigDecimal("2.49"));
            createMenuItem("Paratha", "Indian Breads", new BigDecimal("2.99"));
            createMenuItem("Kulcha", "Indian Breads", new BigDecimal("3.49"));
            createMenuItem("Cheese Naan", "Indian Breads", new BigDecimal("4.49"));

            // Veg Gravies
            createMenuItem("Paneer Butter Masala", "Veg Gravies", new BigDecimal("6.99"));
            createMenuItem("Malai Kofta", "Veg Gravies", new BigDecimal("7.49"));
            createMenuItem("Palak Paneer", "Veg Gravies", new BigDecimal("6.49"));
            createMenuItem("Dal Makhani", "Veg Gravies", new BigDecimal("5.99"));
            createMenuItem("Kadai Paneer", "Veg Gravies", new BigDecimal("6.99"));
            createMenuItem("Chana Masala", "Veg Gravies", new BigDecimal("5.49"));
            createMenuItem("Veg Kolhapuri", "Veg Gravies", new BigDecimal("6.49"));
            createMenuItem("Aloo Gobi", "Veg Gravies", new BigDecimal("4.99"));

            // Non-Veg Gravies
            createMenuItem("Butter Chicken", "Non-Veg Gravies", new BigDecimal("8.99"));
            createMenuItem("Chicken Curry", "Non-Veg Gravies", new BigDecimal("7.99"));
            createMenuItem("Mutton Rogan Josh", "Non-Veg Gravies", new BigDecimal("9.99"));
            createMenuItem("Chicken Tikka Masala", "Non-Veg Gravies", new BigDecimal("8.49"));
            createMenuItem("Fish Curry", "Non-Veg Gravies", new BigDecimal("8.99"));
            createMenuItem("Prawn Masala", "Non-Veg Gravies", new BigDecimal("10.00"));
            createMenuItem("Chicken Korma", "Non-Veg Gravies", new BigDecimal("8.99"));
            createMenuItem("Lamb Vindaloo", "Non-Veg Gravies", new BigDecimal("9.49"));

            // Biryanis
            createMenuItem("Chicken Biryani", "Biryanis", new BigDecimal("7.99"));
            createMenuItem("Mutton Biryani", "Biryanis", new BigDecimal("9.49"));
            createMenuItem("Veg Biryani", "Biryanis", new BigDecimal("6.99"));
            createMenuItem("Prawn Biryani", "Biryanis", new BigDecimal("9.99"));
            createMenuItem("Egg Biryani", "Biryanis", new BigDecimal("7.49"));
            createMenuItem("Hyderabadi Biryani", "Biryanis", new BigDecimal("8.99"));
            createMenuItem("Lucknowi Biryani", "Biryanis", new BigDecimal("9.49"));
            createMenuItem("Jackfruit Biryani", "Biryanis", new BigDecimal("7.49"));

            // Fried Rice & Noodles
            createMenuItem("Veg Fried Rice", "Fried Rice & Noodles", new BigDecimal("5.49"));
            createMenuItem("Chicken Fried Rice", "Fried Rice & Noodles", new BigDecimal("6.99"));
            createMenuItem("Schezwan Fried Rice", "Fried Rice & Noodles", new BigDecimal("5.99"));
            createMenuItem("Hakka Noodles", "Fried Rice & Noodles", new BigDecimal("5.49"));
            createMenuItem("Singapore Noodles", "Fried Rice & Noodles", new BigDecimal("5.99"));
            createMenuItem("Chilli Garlic Noodles", "Fried Rice & Noodles", new BigDecimal("5.49"));
            createMenuItem("Triple Schezwan Rice", "Fried Rice & Noodles", new BigDecimal("7.49"));
            createMenuItem("Paneer Fried Rice", "Fried Rice & Noodles", new BigDecimal("6.49"));

            // Desserts
            createMenuItem("Gulab Jamun", "Desserts", new BigDecimal("3.99"));
            createMenuItem("Rasmalai", "Desserts", new BigDecimal("4.49"));
            createMenuItem("Kulfi", "Desserts", new BigDecimal("3.49"));
            createMenuItem("Ice Cream", "Desserts", new BigDecimal("2.99"));
            createMenuItem("Gajar Ka Halwa", "Desserts", new BigDecimal("4.49"));
            createMenuItem("Jalebi", "Desserts", new BigDecimal("3.49"));
            createMenuItem("Kheer", "Desserts", new BigDecimal("3.99"));
            createMenuItem("Ras Gulla", "Desserts", new BigDecimal("3.99"));

            // Beverages
            createMenuItem("Coca Cola", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Pepsi", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Sprite", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Fanta", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Water Bottle", "Beverages", new BigDecimal("1.00"));
            createMenuItem("Sparkling Water", "Beverages", new BigDecimal("2.49"));
            createMenuItem("Fresh Lime Soda", "Beverages", new BigDecimal("3.49"));
            createMenuItem("Mango Lassi", "Beverages", new BigDecimal("4.99"));
            createMenuItem("Sweet Lassi", "Beverages", new BigDecimal("4.49"));
            createMenuItem("Masala Chai", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Filter Coffee", "Beverages", new BigDecimal("2.99"));
            createMenuItem("Fresh Orange Juice", "Beverages", new BigDecimal("4.99"));

            System.out.println("Created comprehensive Indian menu items with beverages across multiple categories.");
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

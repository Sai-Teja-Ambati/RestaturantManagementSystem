package zeta.foods.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.model.OrderItem;
import zeta.foods.model.Recipe;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the current inventory with date-based validation and tracking
 */
public class CurrentInventory {
    private static final Logger logger = LoggerFactory.getLogger(CurrentInventory.class);

    // Map for storing current inventory
    private static Map<String, Integer> currentInventory = new HashMap<>();

    // File paths
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DATA_DIR = USER_HOME + File.separator + "restaurant_data";
    private static final String CURRENT_INVENTORY_FILE_PATH = DATA_DIR + File.separator + "CurrentInventory.txt";

    // Source resource path for initial data
    private static final String INVENTORY_RESOURCE_PATH = "inventory.txt";

    // Date format
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Static block to initialize current inventory on class load
    static {
        // Create data directory if it doesn't exist
        createDataDirectory();
        initializeCurrentInventory();
    }

    /**
     * Create data directory if it doesn't exist
     */
    private static void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                logger.info("Created data directory at: {}", DATA_DIR);
            } else {
                logger.error("Failed to create data directory at: {}", DATA_DIR);
            }
        }
    }

    /**
     * Initialize the current inventory, validating the date and updating if necessary
     */
    public static void initializeCurrentInventory() {
        logger.info("Initializing current inventory...");

        // Check if the current inventory file exists and if date is current
        if (!isCurrentInventoryUpToDate()) {
            logger.info("Current inventory is not up to date or doesn't exist. Refreshing from main inventory.");
            refreshCurrentInventoryFromMain();
        } else {
            logger.info("Current inventory is up to date. Loading from CurrentInventory.txt");
            loadCurrentInventory();
        }
    }

    /**
     * Check if the current inventory file exists and has today's date
     * @return true if the file exists and the date matches today, false otherwise
     */
    private static boolean isCurrentInventoryUpToDate() {
        try {
            File file = new File(CURRENT_INVENTORY_FILE_PATH);
            if (!file.exists()) {
                logger.info("CurrentInventory.txt doesn't exist at path: {}", CURRENT_INVENTORY_FILE_PATH);
                return false;
            }

            // Read the first line to get the date
            List<String> lines = Files.readAllLines(Paths.get(CURRENT_INVENTORY_FILE_PATH), StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                logger.warn("CurrentInventory.txt is empty.");
                return false;
            }

            // Parse the date from the first line
            String dateString = lines.get(0);
            LocalDate fileDate;
            try {
                fileDate = LocalDate.parse(dateString, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format in CurrentInventory.txt: {}", dateString);
                return false;
            }

            // Compare with today's date
            LocalDate today = LocalDate.now();
            return fileDate.equals(today);

        } catch (IOException e) {
            logger.error("Error checking current inventory file: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Load the current inventory from CurrentInventory.txt
     */
    private static void loadCurrentInventory() {
        logger.info("Loading current inventory from file: {}", CURRENT_INVENTORY_FILE_PATH);
        currentInventory.clear();

        try {
            List<String> lines = Files.readAllLines(Paths.get(CURRENT_INVENTORY_FILE_PATH), StandardCharsets.UTF_8);

            // Skip first two lines (date and empty line)
            if (lines.size() <= 2) {
                logger.warn("CurrentInventory.txt doesn't contain ingredient data.");
                return;
            }

            for (int i = 2; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String ingredient = parts[0].trim();
                    try {
                        int quantity = Integer.parseInt(parts[1].trim());
                        currentInventory.put(ingredient, quantity);
                        logger.debug("Loaded current inventory item: {} - {}", ingredient, quantity);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid quantity format for ingredient: {}", parts[0]);
                    }
                }
            }

            logger.info("Current inventory loaded successfully with {} ingredients", currentInventory.size());

        } catch (IOException e) {
            logger.error("Error loading current inventory from file: {}", e.getMessage(), e);
        }
    }

    /**
     * Refresh the current inventory from the main inventory.txt file
     */
    private static void refreshCurrentInventoryFromMain() {
        logger.info("Refreshing current inventory from main inventory...");
        currentInventory.clear();

        try {
            // First, load from the main inventory resource file
            try (InputStream is = CurrentInventory.class.getClassLoader().getResourceAsStream(INVENTORY_RESOURCE_PATH);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split("-");
                    if (parts.length == 2) {
                        String ingredient = parts[0].trim();
                        try {
                            int quantity = Integer.parseInt(parts[1].trim());
                            currentInventory.put(ingredient, quantity);
                        } catch (NumberFormatException e) {
                            logger.error("Invalid quantity in inventory.txt for: {}", parts[0]);
                        }
                    }
                }
            }

            // Then, save the current inventory with today's date
            saveCurrentInventory();

        } catch (IOException e) {
            logger.error("Error refreshing current inventory: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            logger.error("Inventory resource file not found: {}", INVENTORY_RESOURCE_PATH, e);
        }
    }

    /**
     * Save the current inventory to the CurrentInventory.txt file
     */
    public static void saveCurrentInventory() {
        logger.info("Saving current inventory to file: {}", CURRENT_INVENTORY_FILE_PATH);

        try (FileWriter writer = new FileWriter(CURRENT_INVENTORY_FILE_PATH);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            // Write today's date as the first line
            bufferedWriter.write(LocalDate.now().format(DATE_FORMAT));
            bufferedWriter.newLine();

            // Write an empty line as the second line
            bufferedWriter.newLine();

            // Write all inventory items alphabetically
            currentInventory.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            bufferedWriter.write(entry.getKey() + " - " + entry.getValue());
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            logger.error("Error writing ingredient to file: {}", entry.getKey(), e);
                        }
                    });

            logger.info("Current inventory successfully saved with {} ingredients", currentInventory.size());

        } catch (IOException e) {
            logger.error("Error saving current inventory to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Get the quantity of an ingredient in the current inventory
     *
     * @param ingredient The ingredient name
     * @return The quantity or 0 if not found
     */
    public static int getQuantity(String ingredient) {
        return currentInventory.getOrDefault(ingredient, 0);
    }

    /**
     * Update the quantity of an ingredient in the current inventory
     *
     * @param ingredient The ingredient name
     * @param newQuantity The new quantity
     */
    public static void updateQuantity(String ingredient, int newQuantity) {
        if (newQuantity >= 0) {
            currentInventory.put(ingredient, newQuantity);
            logger.info("Updated current quantity for {}: {}", ingredient, newQuantity);
            // Save changes to file after each update
            saveCurrentInventory();
        } else {
            logger.warn("Attempted to set negative quantity for {}: {}", ingredient, newQuantity);
        }
    }

    /**
     * Check if there are enough ingredients for an order
     *
     * @param orderItems List of order items to check
     * @return true if there are sufficient ingredients, false otherwise
     */
    public static boolean checkIngredientsAvailability(List<OrderItem> orderItems) {
        logger.info("Checking ingredient availability for order with {} items", orderItems.size());

        // Create a map to track required ingredients and quantities
        Map<String, Integer> requiredIngredients = new HashMap<>();

        // Calculate required ingredients
        for (OrderItem item : orderItems) {
            String dishName = item.getItemName();
            int quantity = item.getQuantity();

            // Get recipe for this dish
            Recipe recipe = recipes.getRecipe(dishName);
            if (recipe == null) {
                logger.warn("No recipe found for dish: {}", dishName);
                return false;
            }

            // Add up required ingredients
            for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
                String ingredient = entry.getKey();
                int amountNeeded = entry.getValue() * quantity;

                requiredIngredients.merge(ingredient, amountNeeded, Integer::sum);
            }
        }

        // Check if we have enough of each ingredient
        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int required = entry.getValue();
            int available = getQuantity(ingredient);

            if (available < required) {
                logger.warn("Insufficient quantity of {}: required {}, available {}",
                        ingredient, required, available);
                return false;
            }
        }

        logger.info("All ingredients available for the order");
        return true;
    }

    /**
     * Use ingredients for an order
     *
     * @param orderItems List of order items to prepare
     * @return true if ingredients were successfully used, false otherwise
     */
    public static boolean useIngredientsForOrder(List<OrderItem> orderItems) {
        // First check if we have enough ingredients
        if (!checkIngredientsAvailability(orderItems)) {
            return false;
        }

        logger.info("Using ingredients for order with {} items", orderItems.size());

        // Create a map to track required ingredients and quantities
        Map<String, Integer> requiredIngredients = new HashMap<>();

        // Calculate required ingredients
        for (OrderItem item : orderItems) {
            String dishName = item.getItemName();
            int quantity = item.getQuantity();

            // Get recipe for this dish
            Recipe recipe = recipes.getRecipe(dishName);
            if (recipe == null) {
                logger.warn("No recipe found for dish: {}", dishName);
                return false;
            }

            // Add up required ingredients
            for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
                String ingredient = entry.getKey();
                int amountNeeded = entry.getValue() * quantity;

                requiredIngredients.merge(ingredient, amountNeeded, Integer::sum);
            }
        }

        // Deduct ingredients from inventory
        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int required = entry.getValue();
            int available = getQuantity(ingredient);

            updateQuantity(ingredient, available - required);
        }

        // Save changes to file
        saveCurrentInventory();

        logger.info("Ingredients successfully used for the order");
        return true;
    }

    /**
     * Display the current inventory to the console
     */
    public static void displayCurrentInventory() {
        System.out.println("\n======== Current Inventory ========");
        System.out.println("Date: " + LocalDate.now().format(DATE_FORMAT));
        System.out.println(String.format("%-25s %10s", "Ingredient", "Quantity"));
        System.out.println("------------------------------------");

        currentInventory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(String.format("%-25s %10d", entry.getKey(), entry.getValue())));

        System.out.println("====================================\n");
    }

    /**
     * Reset the current inventory to match the main inventory
     */
    public static void resetCurrentInventory() {
        logger.info("Resetting current inventory to main inventory values");
        refreshCurrentInventoryFromMain();
    }
}

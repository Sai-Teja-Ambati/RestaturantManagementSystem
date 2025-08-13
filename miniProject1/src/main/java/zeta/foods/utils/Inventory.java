package zeta.foods.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static final Logger logger = LoggerFactory.getLogger(Inventory.class);
    public static Map<String, Integer> Inventory = new HashMap<>();
    private static final String INVENTORY_FILE_PATH = "src/main/resources/inventory.txt";

    // Static block to load inventory on class initialization
    static {
        loadInventoryFromFile();
    }

    /**
     * Loads inventory items from the inventory.txt file
     */
    public static void loadInventoryFromFile() {
        logger.info("Loading inventory from file...");

        try (InputStream is = Inventory.class.getClassLoader().getResourceAsStream("inventory.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split the line by hyphen and trim whitespace
                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String ingredient = parts[0].trim();
                    try {
                        int quantity = Integer.parseInt(parts[1].trim());
                        Inventory.put(ingredient, quantity);
                        logger.debug("Loaded ingredient: {} - {}", ingredient, quantity);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid quantity format for ingredient: {}", parts[0]);
                    }
                } else {
                    logger.warn("Invalid line format in inventory file: {}", line);
                }
            }

            logger.info("Inventory loaded successfully with {} ingredients", Inventory.size());

        } catch (IOException e) {
            logger.error("Error loading inventory from file: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            logger.error("Inventory file not found", e);
        }
    }

    /**
     * Saves the current inventory state back to the inventory.txt file
     */
    public static void saveInventoryToFile() {
        logger.info("Saving inventory to file...");

        try (FileWriter writer = new FileWriter(INVENTORY_FILE_PATH);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            // Sort ingredients alphabetically for consistent file structure
            Inventory.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            bufferedWriter.write(entry.getKey() + " - " + entry.getValue());
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            logger.error("Error writing ingredient to file: {}", entry.getKey(), e);
                        }
                    });

            logger.info("Inventory successfully saved with {} ingredients", Inventory.size());

        } catch (IOException e) {
            logger.error("Error saving inventory to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Get the current quantity of an ingredient
     *
     * @param ingredient The ingredient name
     * @return The quantity or 0 if not found
     */
    public static int getQuantity(String ingredient) {
        return Inventory.getOrDefault(ingredient, 0);
    }

    /**
     * Update the quantity of an ingredient
     *
     * @param ingredient The ingredient name
     * @param newQuantity The new quantity
     */
    public static void updateQuantity(String ingredient, int newQuantity) {
        if (newQuantity >= 0) {
            Inventory.put(ingredient, newQuantity);
            logger.info("Updated quantity for {}: {}", ingredient, newQuantity);
        } else {
            logger.warn("Attempted to set negative quantity for {}: {}", ingredient, newQuantity);
        }
    }

    /**
     * Reduce the quantity of an ingredient by a specific amount
     *
     * @param ingredient The ingredient name
     * @param amount The amount to reduce
     * @return true if successful, false if insufficient quantity
     */
    public static boolean useIngredient(String ingredient, int amount) {
        int currentQuantity = getQuantity(ingredient);

        if (currentQuantity >= amount) {
            updateQuantity(ingredient, currentQuantity - amount);
            // Optionally save inventory after each use
            // saveInventoryToFile();
            return true;
        } else {
            logger.warn("Insufficient quantity of {} (requested: {}, available: {})",
                    ingredient, amount, currentQuantity);
            return false;
        }
    }

    /**
     * Add a new ingredient to the inventory
     *
     * @param ingredient The ingredient name
     * @param quantity The initial quantity
     */
    public static void addIngredient(String ingredient, int quantity) {
        if (quantity < 0) {
            logger.warn("Cannot add ingredient with negative quantity: {} - {}", ingredient, quantity);
            return;
        }

        Inventory.put(ingredient, quantity);
        logger.info("Added new ingredient: {} - {}", ingredient, quantity);
        // Optionally save inventory after adding ingredient
        // saveInventoryToFile();
    }

    /**
     * Save changes after multiple inventory operations
     * Call this method after you've made several changes to the inventory
     */
    public static void saveChanges() {
        saveInventoryToFile();
    }

    /**
     * Print the current inventory to the console
     */
    public static void displayInventory() {
        System.out.println("\n======== Current Inventory ========");
        System.out.println(String.format("%-20s %10s", "Ingredient", "Quantity"));
        System.out.println("-----------------------------------");

        Inventory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(String.format("%-20s %10d", entry.getKey(), entry.getValue())));

        System.out.println("===================================\n");
    }
}

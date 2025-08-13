package zeta.foods.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager {
    private static final String DEFAULT_INVENTORY_PATH = "src/main/resources/inventory.txt";
    private static final String CURRENT_INVENTORY_PATH = "src/main/resources/CurrentInventory.txt";

    // Map to store inventory items and quantities
    private static Map<String, Integer> currentInventory = new ConcurrentHashMap<>();
    private static Map<String, Integer> defaultInventory = new ConcurrentHashMap<>();

    static {
        // Load both default and current inventory on startup
        loadDefaultInventory();
        loadCurrentInventory();

        // If current inventory is empty, initialize it with default inventory
        if (currentInventory.isEmpty()) {
            currentInventory.putAll(defaultInventory);
            saveCurrentInventory();
        }
    }

    // Load default inventory from inventory.txt
    private static void loadDefaultInventory() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DEFAULT_INVENTORY_PATH));

            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String ingredient = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    defaultInventory.put(ingredient, quantity);
                }
            }

            System.out.println("Default inventory loaded successfully with " + defaultInventory.size() + " items.");
        } catch (IOException e) {
            System.err.println("Error loading default inventory: " + e.getMessage());
        }
    }

    // Load current inventory from CurrentInventory.txt
    private static void loadCurrentInventory() {
        try {
            Path currentInventoryPath = Paths.get(CURRENT_INVENTORY_PATH);

            if (!Files.exists(currentInventoryPath) || Files.size(currentInventoryPath) == 0) {
                System.out.println("Current inventory file is empty or doesn't exist. Will initialize with default inventory.");
                return;
            }

            List<String> lines = Files.readAllLines(currentInventoryPath);

            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String ingredient = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    currentInventory.put(ingredient, quantity);
                }
            }

            System.out.println("Current inventory loaded successfully with " + currentInventory.size() + " items.");
        } catch (IOException e) {
            System.err.println("Error loading current inventory: " + e.getMessage());
        }
    }

    // Save current inventory to CurrentInventory.txt
    public static void saveCurrentInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CURRENT_INVENTORY_PATH))) {
            for (Map.Entry<String, Integer> entry : currentInventory.entrySet()) {
                writer.write(entry.getKey() + " - " + entry.getValue());
                writer.newLine();
            }
            System.out.println("Current inventory saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving current inventory: " + e.getMessage());
        }
    }

    // Get available quantity of an ingredient
    public static int getAvailableQuantity(String ingredient) {
        return currentInventory.getOrDefault(ingredient, 0);
    }

    // Check if ingredients are available in required quantities
    public static boolean checkIngredientsAvailability(Map<String, Integer> ingredients) {
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (getAvailableQuantity(ingredient) < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    // Update inventory after using ingredients
    public static boolean useIngredients(Map<String, Integer> ingredients) {
        // First check if all ingredients are available
        if (!checkIngredientsAvailability(ingredients)) {
            return false;
        }

        // If all are available, deduct them from inventory
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();

            int currentQuantity = currentInventory.getOrDefault(ingredient, 0);
            currentInventory.put(ingredient, currentQuantity - requiredQuantity);
        }

        // Save the updated inventory
        saveCurrentInventory();
        return true;
    }

    // Get list of missing ingredients
    public static List<String> getMissingIngredients(Map<String, Integer> ingredients) {
        List<String> missingIngredients = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (getAvailableQuantity(ingredient) < requiredQuantity) {
                missingIngredients.add(ingredient + " (Need: " + requiredQuantity +
                                      ", Available: " + getAvailableQuantity(ingredient) + ")");
            }
        }

        return missingIngredients;
    }

    // Reset current inventory to default values
    public static void resetInventory() {
        currentInventory.clear();
        currentInventory.putAll(defaultInventory);
        saveCurrentInventory();
        System.out.println("Inventory reset to default values.");
    }

    // Get a copy of the current inventory
    public static Map<String, Integer> getCurrentInventory() {
        return new HashMap<>(currentInventory);
    }

    // Restock an item to its default quantity
    public static void restockItem(String ingredient) {
        if (defaultInventory.containsKey(ingredient)) {
            currentInventory.put(ingredient, defaultInventory.get(ingredient));
            saveCurrentInventory();
            System.out.println(ingredient + " restocked to default quantity: " + defaultInventory.get(ingredient));
        }
    }
}

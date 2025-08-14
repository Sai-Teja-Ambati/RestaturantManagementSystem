package com.restaurant.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for managing menu items and categories
 * Contains comprehensive menu data with pricing information
 */
public class MenuUtil {
    
    // TreeMap to store menu items by category
    // Structure: <Food Category, Map<Item Name, Price as String>>
    public static final TreeMap<String, Map<String, String>> menuItems = new TreeMap<>();
    
    // Map to store menu items with numeric prices for calculations
    private static final Map<String, Map<String, Double>> menuWithPrices = new LinkedHashMap<>();

    static {
        // Initialize menu categories and items
        initializeMenu();
        initializeMenuWithPrices();
    }

    private static void initializeMenu() {
        // Veg Starters
        Map<String, String> vegStarters = new HashMap<>();
        vegStarters.put("Paneer Tikka", "Rs.249");
        vegStarters.put("Veg Spring Rolls", "Rs.199");
        vegStarters.put("Gobi Manchurian", "Rs.189");
        vegStarters.put("Hara Bhara Kebab", "Rs.219");
        vegStarters.put("Aloo Tikki", "Rs.149");
        vegStarters.put("Crispy Corn", "Rs.179");
        vegStarters.put("Veg Seekh Kebab", "Rs.229");
        menuItems.put("Veg Starters", vegStarters);

        // Non-Veg Starters
        Map<String, String> nonVegStarters = new HashMap<>();
        nonVegStarters.put("Chicken Tikka", "Rs.299");
        nonVegStarters.put("Fish Amritsari", "Rs.329");
        nonVegStarters.put("Mutton Seekh Kebab", "Rs.349");
        nonVegStarters.put("Chicken 65", "Rs.279");
        nonVegStarters.put("Tandoori Prawns", "Rs.399");
        nonVegStarters.put("Chilli Chicken", "Rs.289");
        nonVegStarters.put("Drums of Heaven", "Rs.319");
        menuItems.put("Non-Veg Starters", nonVegStarters);

        // Indian Breads
        Map<String, String> indianBreads = new HashMap<>();
        indianBreads.put("Naan", "Rs.40");
        indianBreads.put("Butter Naan", "Rs.50");
        indianBreads.put("Garlic Naan", "Rs.60");
        indianBreads.put("Roti", "Rs.25");
        indianBreads.put("Butter Roti", "Rs.35");
        indianBreads.put("Paratha", "Rs.45");
        indianBreads.put("Kulcha", "Rs.55");
        menuItems.put("Indian Breads", indianBreads);

        // Veg Gravies
        Map<String, String> vegGravies = new HashMap<>();
        vegGravies.put("Paneer Butter Masala", "Rs.249");
        vegGravies.put("Malai Kofta", "Rs.259");
        vegGravies.put("Palak Paneer", "Rs.239");
        vegGravies.put("Dal Makhani", "Rs.199");
        vegGravies.put("Kadai Paneer", "Rs.249");
        vegGravies.put("Chana Masala", "Rs.189");
        vegGravies.put("Veg Kolhapuri", "Rs.229");
        menuItems.put("Veg Gravies", vegGravies);

        // Non-Veg Gravies
        Map<String, String> nonVegGravies = new HashMap<>();
        nonVegGravies.put("Butter Chicken", "Rs.299");
        nonVegGravies.put("Chicken Curry", "Rs.269");
        nonVegGravies.put("Mutton Rogan Josh", "Rs.349");
        nonVegGravies.put("Chicken Tikka Masala", "Rs.289");
        nonVegGravies.put("Fish Curry", "Rs.319");
        nonVegGravies.put("Prawn Masala", "Rs.359");
        nonVegGravies.put("Chicken Korma", "Rs.299");
        menuItems.put("Non-Veg Gravies", nonVegGravies);

        // Biryani
        Map<String, String> biryanis = new HashMap<>();
        biryanis.put("Chicken Biryani", "Rs.279");
        biryanis.put("Mutton Biryani", "Rs.329");
        biryanis.put("Veg Biryani", "Rs.229");
        biryanis.put("Prawn Biryani", "Rs.349");
        biryanis.put("Egg Biryani", "Rs.249");
        biryanis.put("Hyderabadi Biryani", "Rs.299");
        biryanis.put("Lucknowi Biryani", "Rs.319");
        menuItems.put("Biryanis", biryanis);

        // Fried Rice & Noodles
        Map<String, String> friedRiceNoodles = new HashMap<>();
        friedRiceNoodles.put("Veg Fried Rice", "Rs.189");
        friedRiceNoodles.put("Chicken Fried Rice", "Rs.219");
        friedRiceNoodles.put("Schezwan Fried Rice", "Rs.199");
        friedRiceNoodles.put("Hakka Noodles", "Rs.179");
        friedRiceNoodles.put("Singapore Noodles", "Rs.199");
        friedRiceNoodles.put("Chilli Garlic Noodles", "Rs.189");
        friedRiceNoodles.put("Triple Schezwan Rice", "Rs.249");
        menuItems.put("Fried Rice & Noodles", friedRiceNoodles);

        // Desserts
        Map<String, String> desserts = new HashMap<>();
        desserts.put("Gulab Jamun", "Rs.99");
        desserts.put("Rasmalai", "Rs.129");
        desserts.put("Kulfi", "Rs.89");
        desserts.put("Ice Cream", "Rs.79");
        desserts.put("Gajar Ka Halwa", "Rs.119");
        desserts.put("Jalebi", "Rs.89");
        desserts.put("Rasgulla", "Rs.109");
        menuItems.put("Desserts", desserts);
    }
    
    private static void initializeMenuWithPrices() {
        // Veg Starters
        Map<String, Double> vegStarters = new LinkedHashMap<>();
        vegStarters.put("Paneer Tikka", 249.0);
        vegStarters.put("Veg Spring Rolls", 199.0);
        vegStarters.put("Gobi Manchurian", 189.0);
        vegStarters.put("Hara Bhara Kebab", 219.0);
        vegStarters.put("Aloo Tikki", 149.0);
        vegStarters.put("Crispy Corn", 179.0);
        vegStarters.put("Veg Seekh Kebab", 229.0);
        menuWithPrices.put("Veg Starters", vegStarters);
        
        // Non-Veg Starters
        Map<String, Double> nonVegStarters = new LinkedHashMap<>();
        nonVegStarters.put("Chicken Tikka", 299.0);
        nonVegStarters.put("Fish Amritsari", 329.0);
        nonVegStarters.put("Mutton Seekh Kebab", 349.0);
        nonVegStarters.put("Chicken 65", 279.0);
        nonVegStarters.put("Tandoori Prawns", 399.0);
        nonVegStarters.put("Chilli Chicken", 289.0);
        nonVegStarters.put("Drums of Heaven", 319.0);
        menuWithPrices.put("Non-Veg Starters", nonVegStarters);
        
        // Indian Breads
        Map<String, Double> breads = new LinkedHashMap<>();
        breads.put("Naan", 40.0);
        breads.put("Butter Naan", 50.0);
        breads.put("Garlic Naan", 60.0);
        breads.put("Roti", 25.0);
        breads.put("Butter Roti", 35.0);
        breads.put("Paratha", 45.0);
        breads.put("Kulcha", 55.0);
        menuWithPrices.put("Indian Breads", breads);
        
        // Veg Gravies
        Map<String, Double> vegGravies = new LinkedHashMap<>();
        vegGravies.put("Paneer Butter Masala", 249.0);
        vegGravies.put("Malai Kofta", 259.0);
        vegGravies.put("Palak Paneer", 239.0);
        vegGravies.put("Dal Makhani", 199.0);
        vegGravies.put("Kadai Paneer", 249.0);
        vegGravies.put("Chana Masala", 189.0);
        vegGravies.put("Veg Kolhapuri", 229.0);
        menuWithPrices.put("Veg Gravies", vegGravies);
        
        // Non-Veg Gravies
        Map<String, Double> nonVegGravies = new LinkedHashMap<>();
        nonVegGravies.put("Butter Chicken", 299.0);
        nonVegGravies.put("Chicken Curry", 269.0);
        nonVegGravies.put("Mutton Rogan Josh", 349.0);
        nonVegGravies.put("Chicken Tikka Masala", 289.0);
        nonVegGravies.put("Fish Curry", 319.0);
        nonVegGravies.put("Prawn Masala", 359.0);
        nonVegGravies.put("Chicken Korma", 299.0);
        menuWithPrices.put("Non-Veg Gravies", nonVegGravies);
        
        // Biryani
        Map<String, Double> biryani = new LinkedHashMap<>();
        biryani.put("Chicken Biryani", 279.0);
        biryani.put("Mutton Biryani", 329.0);
        biryani.put("Veg Biryani", 229.0);
        biryani.put("Prawn Biryani", 349.0);
        biryani.put("Egg Biryani", 249.0);
        biryani.put("Hyderabadi Biryani", 299.0);
        biryani.put("Lucknowi Biryani", 319.0);
        menuWithPrices.put("Biryanis", biryani);
        
        // Fried Rice & Noodles
        Map<String, Double> riceNoodles = new LinkedHashMap<>();
        riceNoodles.put("Veg Fried Rice", 189.0);
        riceNoodles.put("Chicken Fried Rice", 219.0);
        riceNoodles.put("Schezwan Fried Rice", 199.0);
        riceNoodles.put("Hakka Noodles", 179.0);
        riceNoodles.put("Singapore Noodles", 199.0);
        riceNoodles.put("Chilli Garlic Noodles", 189.0);
        riceNoodles.put("Triple Schezwan Rice", 249.0);
        menuWithPrices.put("Fried Rice & Noodles", riceNoodles);
        
        // Desserts
        Map<String, Double> desserts = new LinkedHashMap<>();
        desserts.put("Gulab Jamun", 99.0);
        desserts.put("Rasmalai", 129.0);
        desserts.put("Kulfi", 89.0);
        desserts.put("Ice Cream", 79.0);
        desserts.put("Gajar Ka Halwa", 119.0);
        desserts.put("Jalebi", 89.0);
        desserts.put("Rasgulla", 109.0);
        menuWithPrices.put("Desserts", desserts);
    }

    /**
     * Get price of a specific item
     * @param category The menu category
     * @param itemName The item name
     * @return Price as string with Rs. prefix, or null if not found
     */
    public static String getItemPrice(String category, String itemName) {
        Map<String, String> categoryItems = menuItems.get(category);
        if (categoryItems != null) {
            return categoryItems.get(itemName);
        }
        return null;
    }

    /**
     * Get all items in a category
     * @param category The menu category
     * @return Map of item names and prices
     */
    public static Map<String, String> getCategoryItems(String category) {
        return menuItems.get(category);
    }

    /**
     * Helper method to extract numeric price value for calculations
     * @param priceWithRs Price string with Rs. prefix
     * @return Numeric price value
     */
    public static double getPriceValue(String priceWithRs) {
        if (priceWithRs == null || priceWithRs.isEmpty()) {
            return 0.0;
        }
        // Remove "Rs." prefix and parse to double
        return Double.parseDouble(priceWithRs.substring(3));
    }
    
    /**
     * Get numeric price for a specific item
     * @param category The menu category
     * @param itemName The item name
     * @return Numeric price value, or 0.0 if not found
     */
    public static double getItemPriceValue(String category, String itemName) {
        Map<String, Double> categoryItems = menuWithPrices.get(category);
        if (categoryItems != null && categoryItems.containsKey(itemName)) {
            return categoryItems.get(itemName);
        }
        return 0.0;
    }
    
    /**
     * Get price for any menu item by searching all categories
     * @param itemName The item name
     * @return Numeric price value, or 0.0 if not found
     */
    public static double getItemPriceByName(String itemName) {
        for (Map<String, Double> categoryItems : menuWithPrices.values()) {
            if (categoryItems.containsKey(itemName)) {
                return categoryItems.get(itemName);
            }
        }
        return 0.0;
    }
    
    /**
     * Get all menu items with prices
     * @return Map of categories and their items with prices
     */
    public static Map<String, Object> getFullMenu() {
        return new HashMap<>(menuItems);
    }
    
    /**
     * Get menu with numeric prices for calculations
     * @return Map of categories and their items with numeric prices
     */
    public static Map<String, Map<String, Double>> getMenuWithPrices() {
        return new HashMap<>(menuWithPrices);
    }
    
    /**
     * Check if an item exists in the menu
     * @param itemName The item name
     * @return true if item exists, false otherwise
     */
    public static boolean hasMenuItem(String itemName) {
        for (Map<String, String> categoryItems : menuItems.values()) {
            if (categoryItems.containsKey(itemName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get category for a specific menu item
     * @param itemName The item name
     * @return Category name, or null if not found
     */
    public static String getCategoryForItem(String itemName) {
        for (Map.Entry<String, Map<String, String>> category : menuItems.entrySet()) {
            if (category.getValue().containsKey(itemName)) {
                return category.getKey();
            }
        }
        return null;
    }
    
    /**
     * Get all available categories
     * @return Set of category names
     */
    public static java.util.Set<String> getAllCategories() {
        return menuItems.keySet();
    }
}
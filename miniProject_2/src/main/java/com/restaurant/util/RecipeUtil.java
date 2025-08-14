package com.restaurant.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import static java.util.Map.entry;

/**
 * Utility class for managing recipes and their ingredients
 * Contains comprehensive recipe data for all menu items
 */
public class RecipeUtil {
    
    // TreeMap to store recipes with their ingredients and quantities
    // Structure: <Item, <Ingredient, Quantity>>
    public static final TreeMap<String, Map<String, Integer>> recipeDetails = new TreeMap<>();
    
    static {
        // Initialize recipes
        initializeRecipes();
    }
    
    private static void initializeRecipes() {
        // Veg Starters
        addRecipe("Paneer Tikka", Map.of(
            "Paneer", 250, 
            "Yogurt", 100, 
            "Red Chilli Powder", 5, 
            "Garam Masala", 5, 
            "Ginger Garlic Paste", 15, 
            "Oil", 20
        ));
        
        addRecipe("Veg Spring Rolls", Map.ofEntries(
            entry("All Purpose Flour", 150),
            entry("Mixed Vegetables", 200),
            entry("Cabbage", 100),
            entry("Carrot", 50),
            entry("Capsicum", 50),
            entry("Spring Onion", 30),
            entry("Soy Sauce", 10),
            entry("Oil", 30)
        ));
        
        addRecipe("Gobi Manchurian", Map.of(
            "Cauliflower", 300, 
            "Cornflour", 50, 
            "All Purpose Flour", 50, 
            "Soy Sauce", 15, 
            "Ginger Garlic Paste", 15, 
            "Green Chillies", 10, 
            "Onion", 50
        ));
        
        addRecipe("Hara Bhara Kebab", Map.ofEntries(
            entry("Spinach", 150),
            entry("Peas", 100),
            entry("Potato", 100),
            entry("Green Chillies", 10),
            entry("Ginger", 10),
            entry("Coriander Leaves", 20),
            entry("Breadcrumbs", 30),
            entry("Oil", 20)
        ));
        
        addRecipe("Aloo Tikki", Map.of(
            "Potato", 300,
            "Green Peas", 50,
            "Coriander Leaves", 20,
            "Cumin Powder", 5,
            "Garam Masala", 5,
            "Breadcrumbs", 30,
            "Oil", 20
        ));
        
        addRecipe("Crispy Corn", Map.of(
            "Corn Kernels", 250,
            "Cornflour", 30,
            "All Purpose Flour", 30,
            "Red Chilli Powder", 5,
            "Black Pepper", 5,
            "Oil", 100
        ));
        
        addRecipe("Veg Seekh Kebab", Map.ofEntries(
            entry("Mixed Vegetables", 200),
            entry("Potato", 100),
            entry("Paneer", 100),
            entry("Ginger Garlic Paste", 15),
            entry("Garam Masala", 5),
            entry("Coriander Leaves", 20),
            entry("Breadcrumbs", 30),
            entry("Oil", 20)
        ));
        
        // Non-Veg Starters
        addRecipe("Chicken Tikka", Map.of(
            "Chicken", 300, 
            "Yogurt", 100, 
            "Lemon Juice", 10, 
            "Red Chilli Powder", 5, 
            "Garam Masala", 5, 
            "Ginger Garlic Paste", 15, 
            "Oil", 20
        ));
        
        addRecipe("Fish Amritsari", Map.of(
            "Fish", 250, 
            "Gram Flour", 100, 
            "Carom Seeds", 5, 
            "Lemon Juice", 10, 
            "Red Chilli Powder", 5, 
            "Oil", 50
        ));
        
        addRecipe("Mutton Seekh Kebab", Map.ofEntries(
            entry("Minced Mutton", 300),
            entry("Onion", 50),
            entry("Green Chillies", 10),
            entry("Ginger Garlic Paste", 15),
            entry("Garam Masala", 5),
            entry("Coriander Leaves", 20),
            entry("Mint Leaves", 10),
            entry("Oil", 20)
        ));
        
        addRecipe("Chicken 65", Map.ofEntries(
            entry("Chicken", 300),
            entry("Yogurt", 50),
            entry("Red Chilli Powder", 10),
            entry("Ginger Garlic Paste", 15),
            entry("Curry Leaves", 10),
            entry("Cornflour", 30),
            entry("All Purpose Flour", 30),
            entry("Oil", 100)
        ));
        
        addRecipe("Tandoori Prawns", Map.of(
            "Prawns", 250,
            "Yogurt", 50,
            "Red Chilli Powder", 5,
            "Garam Masala", 5,
            "Ginger Garlic Paste", 15,
            "Lemon Juice", 10,
            "Oil", 20
        ));
        
        addRecipe("Chilli Chicken", Map.of(
            "Chicken", 300,
            "Capsicum", 50,
            "Onion", 50,
            "Soy Sauce", 15,
            "Chilli Sauce", 15,
            "Vinegar", 10,
            "Cornflour", 20,
            "Oil", 30
        ));
        
        addRecipe("Drums of Heaven", Map.ofEntries(
            entry("Chicken Wings", 300),
            entry("Soy Sauce", 20),
            entry("Chilli Sauce", 20),
            entry("Vinegar", 10),
            entry("Honey", 15),
            entry("Ginger Garlic Paste", 15),
            entry("Cornflour", 30),
            entry("Oil", 100)
        ));
        
        // Indian Breads
        addRecipe("Naan", Map.of(
            "All Purpose Flour", 200, 
            "Yogurt", 50, 
            "Milk", 30, 
            "Baking Powder", 5, 
            "Sugar", 5, 
            "Butter", 15
        ));
        
        addRecipe("Butter Naan", Map.of(
            "All Purpose Flour", 200,
            "Yogurt", 50,
            "Milk", 30,
            "Baking Powder", 5,
            "Sugar", 5,
            "Butter", 30
        ));
        
        addRecipe("Garlic Naan", Map.of(
            "All Purpose Flour", 200,
            "Yogurt", 50,
            "Milk", 30,
            "Baking Powder", 5,
            "Sugar", 5,
            "Butter", 15,
            "Garlic", 20,
            "Coriander Leaves", 10
        ));
        
        addRecipe("Roti", Map.of(
            "Wheat Flour", 150,
            "Water", 100
        ));
        
        addRecipe("Butter Roti", Map.of(
            "Wheat Flour", 150, 
            "Water", 100, 
            "Butter", 15
        ));
        
        addRecipe("Paratha", Map.of(
            "Wheat Flour", 150,
            "Water", 100,
            "Ghee", 20
        ));
        
        addRecipe("Kulcha", Map.of(
            "All Purpose Flour", 200,
            "Yogurt", 30,
            "Baking Powder", 5,
            "Baking Soda", 3,
            "Milk", 30,
            "Butter", 15
        ));
        
        // Veg Gravies
        addRecipe("Paneer Butter Masala", Map.ofEntries(
            entry("Paneer", 250),
            entry("Tomato", 150),
            entry("Onion", 100),
            entry("Cream", 50),
            entry("Butter", 30),
            entry("Ginger Garlic Paste", 15),
            entry("Red Chilli Powder", 5),
            entry("Garam Masala", 5)
        ));
        
        addRecipe("Malai Kofta", Map.ofEntries(
            entry("Paneer", 150),
            entry("Potato", 100),
            entry("Cashew Nuts", 30),
            entry("Raisins", 10),
            entry("Cornflour", 20),
            entry("Cream", 50),
            entry("Tomato", 100),
            entry("Onion", 50),
            entry("Butter", 20),
            entry("Garam Masala", 5)
        ));
        
        addRecipe("Palak Paneer", Map.of(
            "Paneer", 200, 
            "Spinach", 300, 
            "Onion", 50, 
            "Ginger Garlic Paste", 15, 
            "Green Chillies", 10, 
            "Cream", 30, 
            "Butter", 20
        ));
        
        addRecipe("Dal Makhani", Map.of(
            "Black Lentils", 200,
            "Kidney Beans", 50,
            "Onion", 50,
            "Tomato", 100,
            "Butter", 30,
            "Cream", 30,
            "Ginger Garlic Paste", 15,
            "Garam Masala", 5
        ));
        
        addRecipe("Kadai Paneer", Map.of(
            "Paneer", 250,
            "Capsicum", 100,
            "Onion", 100,
            "Tomato", 100,
            "Kadai Masala", 10,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Butter", 20
        ));
        
        addRecipe("Chana Masala", Map.of(
            "Chickpeas", 250,
            "Onion", 100,
            "Tomato", 100,
            "Ginger Garlic Paste", 15,
            "Chole Masala", 10,
            "Red Chilli Powder", 5,
            "Garam Masala", 5,
            "Oil", 20
        ));
        
        addRecipe("Veg Kolhapuri", Map.of(
            "Mixed Vegetables", 300,
            "Onion", 100,
            "Tomato", 100,
            "Coconut", 30,
            "Kolhapuri Masala", 10,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Oil", 20
        ));
        
        // Non-Veg Gravies
        addRecipe("Butter Chicken", Map.of(
            "Chicken", 300, 
            "Tomato", 200, 
            "Onion", 100, 
            "Cream", 50, 
            "Butter", 30, 
            "Ginger Garlic Paste", 15, 
            "Red Chilli Powder", 5, 
            "Garam Masala", 5
        ));
        
        addRecipe("Chicken Curry", Map.of(
            "Chicken", 300, 
            "Onion", 150, 
            "Tomato", 100, 
            "Coconut Milk", 100, 
            "Ginger Garlic Paste", 15, 
            "Curry Powder", 10, 
            "Oil", 20
        ));
        
        addRecipe("Mutton Rogan Josh", Map.of(
            "Mutton", 300,
            "Yogurt", 100,
            "Onion", 100,
            "Tomato", 100,
            "Rogan Josh Masala", 15,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Garam Masala", 5,
            "Oil", 30
        ));
        
        addRecipe("Chicken Tikka Masala", Map.of(
            "Chicken Tikka", 300,
            "Onion", 100,
            "Tomato", 150,
            "Cream", 50,
            "Butter", 20,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Garam Masala", 5
        ));
        
        addRecipe("Fish Curry", Map.of(
            "Fish", 300,
            "Onion", 100,
            "Tomato", 100,
            "Coconut Milk", 100,
            "Curry Leaves", 5,
            "Mustard Seeds", 5,
            "Ginger Garlic Paste", 15,
            "Turmeric Powder", 5,
            "Oil", 20
        ));
        
        addRecipe("Prawn Masala", Map.of(
            "Prawns", 250,
            "Onion", 100,
            "Tomato", 100,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Garam Masala", 5,
            "Oil", 20
        ));
        
        addRecipe("Chicken Korma", Map.of(
            "Chicken", 300,
            "Yogurt", 100,
            "Onion", 100,
            "Cashew Nuts", 50,
            "Cream", 50,
            "Korma Masala", 10,
            "Ginger Garlic Paste", 15,
            "Oil", 20
        ));
        
        // Biryani
        addRecipe("Chicken Biryani", Map.of(
            "Chicken", 300, 
            "Basmati Rice", 250, 
            "Onion", 100, 
            "Yogurt", 50, 
            "Ginger Garlic Paste", 15, 
            "Biryani Masala", 10, 
            "Saffron", 1, 
            "Ghee", 30
        ));
        
        addRecipe("Mutton Biryani", Map.of(
            "Mutton", 300,
            "Basmati Rice", 250,
            "Onion", 100,
            "Yogurt", 50,
            "Ginger Garlic Paste", 15,
            "Biryani Masala", 10,
            "Saffron", 1,
            "Ghee", 30
        ));
        
        addRecipe("Veg Biryani", Map.of(
            "Basmati Rice", 250, 
            "Mixed Vegetables", 300, 
            "Onion", 100, 
            "Yogurt", 50, 
            "Ginger Garlic Paste", 15, 
            "Biryani Masala", 10, 
            "Saffron", 1, 
            "Ghee", 30
        ));
        
        addRecipe("Prawn Biryani", Map.of(
            "Prawns", 250,
            "Basmati Rice", 250,
            "Onion", 100,
            "Yogurt", 50,
            "Ginger Garlic Paste", 15,
            "Biryani Masala", 10,
            "Saffron", 1,
            "Ghee", 30
        ));
        
        addRecipe("Egg Biryani", Map.of(
            "Eggs", 6,
            "Basmati Rice", 250,
            "Onion", 100,
            "Yogurt", 50,
            "Ginger Garlic Paste", 15,
            "Biryani Masala", 10,
            "Saffron", 1,
            "Ghee", 30
        ));
        
        addRecipe("Hyderabadi Biryani", Map.ofEntries(
            entry("Chicken", 300),
            entry("Basmati Rice", 250),
            entry("Onion", 150),
            entry("Yogurt", 100),
            entry("Mint Leaves", 20),
            entry("Coriander Leaves", 20),
            entry("Green Chillies", 10),
            entry("Ginger Garlic Paste", 20),
            entry("Biryani Masala", 15),
            entry("Saffron", 2),
            entry("Ghee", 40)
        ));
        
        addRecipe("Lucknowi Biryani", Map.of(
            "Chicken", 300,
            "Basmati Rice", 250,
            "Onion", 100,
            "Yogurt", 50,
            "Cream", 30,
            "Kewra Water", 5,
            "Ginger Garlic Paste", 15,
            "Biryani Masala", 10,
            "Saffron", 1,
            "Ghee", 30
        ));
        
        // Fried Rice & Noodles
        addRecipe("Veg Fried Rice", Map.of(
            "Rice", 250, 
            "Mixed Vegetables", 200, 
            "Soy Sauce", 15, 
            "Spring Onion", 30, 
            "Garlic", 10, 
            "Oil", 30
        ));
        
        addRecipe("Chicken Fried Rice", Map.of(
            "Rice", 250,
            "Chicken", 150,
            "Mixed Vegetables", 100,
            "Soy Sauce", 15,
            "Spring Onion", 30,
            "Garlic", 10,
            "Oil", 30
        ));
        
        addRecipe("Schezwan Fried Rice", Map.of(
            "Rice", 250,
            "Mixed Vegetables", 150,
            "Schezwan Sauce", 30,
            "Soy Sauce", 10,
            "Spring Onion", 30,
            "Garlic", 15,
            "Oil", 30
        ));
        
        addRecipe("Hakka Noodles", Map.of(
            "Noodles", 250, 
            "Cabbage", 50, 
            "Carrot", 50, 
            "Capsicum", 50, 
            "Soy Sauce", 15, 
            "Vinegar", 10, 
            "Garlic", 10, 
            "Oil", 30
        ));
        
        addRecipe("Singapore Noodles", Map.of(
            "Noodles", 250,
            "Mixed Vegetables", 150,
            "Curry Powder", 10,
            "Soy Sauce", 15,
            "Spring Onion", 30,
            "Garlic", 10,
            "Oil", 30
        ));
        
        addRecipe("Chilli Garlic Noodles", Map.of(
            "Noodles", 250,
            "Mixed Vegetables", 100,
            "Garlic", 30,
            "Red Chilli Sauce", 20,
            "Soy Sauce", 15,
            "Vinegar", 10,
            "Oil", 30
        ));
        
        addRecipe("Triple Schezwan Rice", Map.of(
            "Rice", 200,
            "Noodles", 100,
            "Mixed Vegetables", 150,
            "Schezwan Sauce", 40,
            "Soy Sauce", 15,
            "Spring Onion", 30,
            "Garlic", 15,
            "Oil", 40
        ));
        
        // Desserts
        addRecipe("Gulab Jamun", Map.of(
            "Milk Powder", 100, 
            "All Purpose Flour", 30, 
            "Ghee", 15, 
            "Baking Powder", 2, 
            "Sugar", 200, 
            "Water", 100, 
            "Cardamom Powder", 2
        ));
        
        addRecipe("Rasmalai", Map.of(
            "Milk", 1000, 
            "Sugar", 200, 
            "Lemon Juice", 15, 
            "Cardamom Powder", 2, 
            "Saffron", 1, 
            "Pistachios", 10
        ));
        
        addRecipe("Kulfi", Map.of(
            "Milk", 1000,
            "Sugar", 150,
            "Condensed Milk", 100,
            "Pistachios", 20,
            "Almonds", 20,
            "Cardamom Powder", 2
        ));
        
        addRecipe("Ice Cream", Map.of(
            "Cream", 250,
            "Milk", 250,
            "Sugar", 100,
            "Vanilla Extract", 5
        ));
        
        addRecipe("Gajar Ka Halwa", Map.of(
            "Carrot", 500,
            "Milk", 500,
            "Sugar", 150,
            "Ghee", 50,
            "Cardamom Powder", 2,
            "Nuts", 30
        ));
        
        addRecipe("Jalebi", Map.of(
            "All Purpose Flour", 200,
            "Yogurt", 30,
            "Sugar", 300,
            "Water", 150,
            "Saffron", 1,
            "Cardamom Powder", 2,
            "Oil", 200
        ));
        
        addRecipe("Rasgulla", Map.of(
            "Milk", 1000,
            "Lemon Juice", 20,
            "Sugar", 250,
            "Water", 200,
            "Cardamom Powder", 2
        ));
    }
    
    private static void addRecipe(String item, Map<String, Integer> ingredients) {
        recipeDetails.put(item, new HashMap<>(ingredients));
    }
    
    /**
     * Get recipe ingredients for a specific dish
     * @param dishName The name of the dish
     * @return Map of ingredients and their quantities, or null if recipe not found
     */
    public static Map<String, Integer> getRecipeIngredients(String dishName) {
        return recipeDetails.get(dishName);
    }
    
    /**
     * Check if a recipe exists for the given dish
     * @param dishName The name of the dish
     * @return true if recipe exists, false otherwise
     */
    public static boolean hasRecipe(String dishName) {
        return recipeDetails.containsKey(dishName);
    }
    
    /**
     * Get all recipes
     * @return TreeMap containing all recipes
     */
    public static TreeMap<String, Map<String, Integer>> getAllRecipes() {
        return recipeDetails;
    }
    
    /**
     * Get recipe details for a specific item
     * @param item The menu item name
     * @return Map of ingredients and quantities
     */
    public static Map<String, Integer> getRecipeByItem(String item) {
        return recipeDetails.get(item);
    }
}
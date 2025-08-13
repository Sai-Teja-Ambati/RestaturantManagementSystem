package com.restaurant.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class RecipeUtil {
    
    // TreeMap to store recipes with their ingredients and quantities
    private static final TreeMap<String, Map<String, Integer>> recipeDetails = new TreeMap<>();
    
    // Menu with prices
    private static final Map<String, Map<String, Double>> menuWithPrices = new LinkedHashMap<>();
    
    static {
        initializeRecipes();
        initializeMenuWithPrices();
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
        
        addRecipe("Veg Spring Rolls", Map.of(
            "All Purpose Flour", 150,
            "Mixed Vegetables", 200,
            "Cabbage", 100,
            "Carrot", 50,
            "Capsicum", 50,
            "Spring Onion", 30,
            "Soy Sauce", 10,
            "Oil", 30
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
        
        addRecipe("Hara Bhara Kebab", Map.of(
            "Spinach", 150,
            "Peas", 100,
            "Potato", 100,
            "Green Chillies", 10,
            "Ginger", 10,
            "Coriander Leaves", 20,
            "Breadcrumbs", 30,
            "Oil", 20
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
        
        // Veg Gravies
        addRecipe("Paneer Butter Masala", Map.of(
            "Paneer", 250,
            "Tomato", 150,
            "Onion", 100,
            "Cream", 50,
            "Butter", 30,
            "Ginger Garlic Paste", 15,
            "Red Chilli Powder", 5,
            "Garam Masala", 5
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
        
        addRecipe("Kulfi", Map.of(
            "Milk", 1000,
            "Sugar", 150,
            "Condensed Milk", 100,
            "Pistachios", 20,
            "Almonds", 20,
            "Cardamom Powder", 2
        ));
    }
    
    private static void initializeMenuWithPrices() {
        // Veg Starters
        Map<String, Double> vegStarters = new LinkedHashMap<>();
        vegStarters.put("Paneer Tikka", 249.0);
        vegStarters.put("Veg Spring Rolls", 199.0);
        vegStarters.put("Gobi Manchurian", 179.0);
        vegStarters.put("Hara Bhara Kebab", 189.0);
        vegStarters.put("Aloo Tikki", 149.0);
        menuWithPrices.put("Veg Starters", vegStarters);
        
        // Non-Veg Starters
        Map<String, Double> nonVegStarters = new LinkedHashMap<>();
        nonVegStarters.put("Chicken Tikka", 299.0);
        nonVegStarters.put("Fish Amritsari", 349.0);
        menuWithPrices.put("Non-Veg Starters", nonVegStarters);
        
        // Indian Breads
        Map<String, Double> breads = new LinkedHashMap<>();
        breads.put("Naan", 45.0);
        breads.put("Butter Naan", 50.0);
        breads.put("Garlic Naan", 60.0);
        breads.put("Roti", 25.0);
        menuWithPrices.put("Indian Breads", breads);
        
        // Veg Gravies
        Map<String, Double> vegGravies = new LinkedHashMap<>();
        vegGravies.put("Paneer Butter Masala", 279.0);
        vegGravies.put("Palak Paneer", 259.0);
        vegGravies.put("Dal Makhani", 199.0);
        menuWithPrices.put("Veg Gravies", vegGravies);
        
        // Non-Veg Gravies
        Map<String, Double> nonVegGravies = new LinkedHashMap<>();
        nonVegGravies.put("Butter Chicken", 329.0);
        nonVegGravies.put("Chicken Curry", 299.0);
        menuWithPrices.put("Non-Veg Gravies", nonVegGravies);
        
        // Biryani
        Map<String, Double> biryani = new LinkedHashMap<>();
        biryani.put("Chicken Biryani", 349.0);
        biryani.put("Veg Biryani", 279.0);
        menuWithPrices.put("Biryani", biryani);
        
        // Fried Rice & Noodles
        Map<String, Double> riceNoodles = new LinkedHashMap<>();
        riceNoodles.put("Veg Fried Rice", 199.0);
        riceNoodles.put("Chicken Fried Rice", 249.0);
        riceNoodles.put("Hakka Noodles", 189.0);
        menuWithPrices.put("Fried Rice & Noodles", riceNoodles);
        
        // Desserts
        Map<String, Double> desserts = new LinkedHashMap<>();
        desserts.put("Gulab Jamun", 89.0);
        desserts.put("Kulfi", 99.0);
        menuWithPrices.put("Desserts", desserts);
    }
    
    private static void addRecipe(String item, Map<String, Integer> ingredients) {
        recipeDetails.put(item, new HashMap<>(ingredients));
    }
    
    public static Map<String, Integer> getRecipeIngredients(String dishName) {
        return recipeDetails.get(dishName);
    }
    
    public static boolean hasRecipe(String dishName) {
        return recipeDetails.containsKey(dishName);
    }
    
    public static Map<String, Object> getMenuWithPrices() {
        return new HashMap<>(menuWithPrices);
    }
    
    public static TreeMap<String, Map<String, Integer>> getAllRecipes() {
        return recipeDetails;
    }
}
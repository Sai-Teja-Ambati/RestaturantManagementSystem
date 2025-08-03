package zeta.foods.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class menu {
    // TreeMap to store menu items by category
    // Structure: <Food Category, Map<Item Name, Price as String>>
    public static final TreeMap<String, Map<String, String>> menuItems = new TreeMap<>();

    static {
        // Initialize menu categories and items
        initializeMenu();
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
        menuItems.put("Desserts", desserts);
    }

    // Method to get price of a specific item
    public static String getItemPrice(String category, String itemName) {
        Map<String, String> categoryItems = menuItems.get(category);
        if (categoryItems != null) {
            return categoryItems.get(itemName);
        }
        return null;
    }

    // Method to get all items in a category
    public static Map<String, String> getCategoryItems(String category) {
        return menuItems.get(category);
    }

    // Helper method to extract numeric price value if needed for calculations
    public static int getPriceValue(String priceWithRs) {
        if (priceWithRs == null || priceWithRs.isEmpty()) {
            return 0;
        }
        // Remove "Rs." prefix and parse to int
        return Integer.parseInt(priceWithRs.substring(3));
    }
}

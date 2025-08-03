package zeta.foods.utils;

import java.util.ArrayList;
import java.util.TreeMap;

public class menu {
    // TreeMap to store menu items by category
    // Structure: <Food Category, List of Items in that Category>
    public static final TreeMap<String, ArrayList<String>> menuItems = new TreeMap<>();

    static {
        // Initialize menu categories and items
        initializeMenu();
    }

    private static void initializeMenu() {
        // Veg Starters
        ArrayList<String> vegStarters = new ArrayList<>();
        vegStarters.add("Paneer Tikka");
        vegStarters.add("Veg Spring Rolls");
        vegStarters.add("Gobi Manchurian");
        vegStarters.add("Hara Bhara Kebab");
        vegStarters.add("Aloo Tikki");
        vegStarters.add("Crispy Corn");
        vegStarters.add("Veg Seekh Kebab");
        menuItems.put("Veg Starters", vegStarters);

        // Non-Veg Starters
        ArrayList<String> nonVegStarters = new ArrayList<>();
        nonVegStarters.add("Chicken Tikka");
        nonVegStarters.add("Fish Amritsari");
        nonVegStarters.add("Mutton Seekh Kebab");
        nonVegStarters.add("Chicken 65");
        nonVegStarters.add("Tandoori Prawns");
        nonVegStarters.add("Chilli Chicken");
        nonVegStarters.add("Drums of Heaven");
        menuItems.put("Non-Veg Starters", nonVegStarters);

        // Indian Breads
        ArrayList<String> indianBreads = new ArrayList<>();
        indianBreads.add("Naan");
        indianBreads.add("Butter Naan");
        indianBreads.add("Garlic Naan");
        indianBreads.add("Roti");
        indianBreads.add("Butter Roti");
        indianBreads.add("Paratha");
        indianBreads.add("Kulcha");
        menuItems.put("Indian Breads", indianBreads);

        // Veg Gravies
        ArrayList<String> vegGravies = new ArrayList<>();
        vegGravies.add("Paneer Butter Masala");
        vegGravies.add("Malai Kofta");
        vegGravies.add("Palak Paneer");
        vegGravies.add("Dal Makhani");
        vegGravies.add("Kadai Paneer");
        vegGravies.add("Chana Masala");
        vegGravies.add("Veg Kolhapuri");
        menuItems.put("Veg Gravies", vegGravies);

        // Non-Veg Gravies
        ArrayList<String> nonVegGravies = new ArrayList<>();
        nonVegGravies.add("Butter Chicken");
        nonVegGravies.add("Chicken Curry");
        nonVegGravies.add("Mutton Rogan Josh");
        nonVegGravies.add("Chicken Tikka Masala");
        nonVegGravies.add("Fish Curry");
        nonVegGravies.add("Prawn Masala");
        nonVegGravies.add("Chicken Korma");
        menuItems.put("Non-Veg Gravies", nonVegGravies);

        // Biryani
        ArrayList<String> biryanis = new ArrayList<>();
        biryanis.add("Chicken Biryani");
        biryanis.add("Mutton Biryani");
        biryanis.add("Veg Biryani");
        biryanis.add("Prawn Biryani");
        biryanis.add("Egg Biryani");
        biryanis.add("Hyderabadi Biryani");
        biryanis.add("Lucknowi Biryani");
        menuItems.put("Biryanis", biryanis);

        // Fried Rice & Noodles
        ArrayList<String> friedRiceNoodles = new ArrayList<>();
        friedRiceNoodles.add("Veg Fried Rice");
        friedRiceNoodles.add("Chicken Fried Rice");
        friedRiceNoodles.add("Schezwan Fried Rice");
        friedRiceNoodles.add("Hakka Noodles");
        friedRiceNoodles.add("Singapore Noodles");
        friedRiceNoodles.add("Chilli Garlic Noodles");
        friedRiceNoodles.add("Triple Schezwan Rice");
        menuItems.put("Fried Rice & Noodles", friedRiceNoodles);

        // Desserts
        ArrayList<String> desserts = new ArrayList<>();
        desserts.add("Gulab Jamun");
        desserts.add("Rasmalai");
        desserts.add("Kulfi");
        desserts.add("Ice Cream");
        desserts.add("Gajar Ka Halwa");
        desserts.add("Jalebi");
        desserts.add("Rasgulla");
        menuItems.put("Desserts", desserts);
    }

    // Method to get all menu items
    public static TreeMap<String, ArrayList<String>> getMenuItems() {
        return menuItems;
    }

    // Method to get items by category
    public static ArrayList<String> getItemsByCategory(String category) {
        return menuItems.get(category);
    }
}

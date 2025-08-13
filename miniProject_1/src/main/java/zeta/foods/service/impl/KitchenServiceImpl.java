package zeta.foods.service.impl;

import zeta.foods.model.OrderStatus;
import zeta.foods.service.KitchenService;
import zeta.foods.utils.InventoryManager;
import zeta.foods.utils.recipes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KitchenServiceImpl implements KitchenService {

    // LiveOrders structure: <Table Number, <Item Name, Status>>
    private final Map<String, Map<String, OrderStatus>> liveOrders = new ConcurrentHashMap<>();

    @Override
    public boolean placeOrder(String tableNumber, Map<String, Integer> items) {
        try {
            if (tableNumber == null || items == null || items.isEmpty()) {
                return false;
            }

            Map<String, OrderStatus> tableOrders = liveOrders.getOrDefault(tableNumber, new HashMap<>());

            // Add each item to the order with quantity
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey();
                Integer quantity = entry.getValue();

                // If there are multiple quantities, we add them with a suffix
                if (quantity > 1) {
                    for (int i = 1; i <= quantity; i++) {
                        tableOrders.put(itemName + " #" + i, OrderStatus.RECEIVED);
                    }
                } else {
                    tableOrders.put(itemName, OrderStatus.RECEIVED);
                }
            }

            liveOrders.put(tableNumber, tableOrders);
            return true;
        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateOrderStatus(String tableNumber, String item, OrderStatus status) {
        try {
            if (!liveOrders.containsKey(tableNumber)) {
                return false;
            }

            Map<String, OrderStatus> tableOrders = liveOrders.get(tableNumber);
            if (!tableOrders.containsKey(item)) {
                return false;
            }

            tableOrders.put(item, status);

            // If all items are served or cancelled, we can remove the order
            boolean allCompleted = tableOrders.values().stream()
                .allMatch(s -> s == OrderStatus.SERVED || s == OrderStatus.CANCELLED);

            if (allCompleted) {
                liveOrders.remove(tableNumber);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Map<String, OrderStatus>> getAllLiveOrders() {
        return new HashMap<>(liveOrders);
    }

    @Override
    public Map<String, OrderStatus> getOrdersByTable(String tableNumber) {
        return liveOrders.getOrDefault(tableNumber, new HashMap<>());
    }

    @Override
    public boolean cancelOrder(String tableNumber) {
        try {
            if (!liveOrders.containsKey(tableNumber)) {
                return false;
            }

            Map<String, OrderStatus> tableOrders = liveOrders.get(tableNumber);
            for (String item : tableOrders.keySet()) {
                tableOrders.put(item, OrderStatus.CANCELLED);
            }

            liveOrders.remove(tableNumber);
            return true;
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String cookFoodItem(String tableNumber, String itemName) {
        try {
            // Check if table and order exist
            if (!liveOrders.containsKey(tableNumber)) {
                return "Table " + tableNumber + " has no active orders";
            }

            Map<String, OrderStatus> tableOrders = liveOrders.get(tableNumber);
            if (!tableOrders.containsKey(itemName)) {
                return "Item " + itemName + " is not in the order for table " + tableNumber;
            }

            // Get the base item name (remove any quantity suffix like "#1")
            String baseItemName = itemName.split(" #")[0];

            // Get recipe for the item
            Map<String, Integer> recipeIngredients = recipes.getRecipeByItem(baseItemName);
            if (recipeIngredients == null) {
                return "Recipe not found for item: " + baseItemName;
            }

            // Check if all ingredients are available
            if (!InventoryManager.checkIngredientsAvailability(recipeIngredients)) {
                List<String> missingIngredients = InventoryManager.getMissingIngredients(recipeIngredients);
                return "Cannot cook " + baseItemName + ". Missing ingredients: " + String.join(", ", missingIngredients);
            }

            // Update the order status to IN_PREPARATION
            tableOrders.put(itemName, OrderStatus.IN_PREPARATION);

            // Use the ingredients from inventory
            InventoryManager.useIngredients(recipeIngredients);

            // Update order status to READY
            tableOrders.put(itemName, OrderStatus.READY);

            return baseItemName + " has been successfully prepared for table " + tableNumber;
        } catch (Exception e) {
            System.err.println("Error cooking item: " + e.getMessage());
            return "Error occurred while cooking " + itemName + ": " + e.getMessage();
        }
    }

    @Override
    public Map<String, String> cookAllItemsForTable(String tableNumber) {
        Map<String, String> results = new HashMap<>();

        if (!liveOrders.containsKey(tableNumber)) {
            results.put("Error", "Table " + tableNumber + " has no active orders");
            return results;
        }

        Map<String, OrderStatus> tableOrders = liveOrders.get(tableNumber);
        for (String item : tableOrders.keySet()) {
            if (tableOrders.get(item) == OrderStatus.RECEIVED) {
                String result = cookFoodItem(tableNumber, item);
                results.put(item, result);
            }
        }

        return results;
    }
}

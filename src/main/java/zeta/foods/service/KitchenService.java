package zeta.foods.service;

import zeta.foods.model.OrderStatus;

import java.util.Map;

public interface KitchenService {
    /**
     * Place a new order for a table
     * @param tableNumber Table number
     * @param items Map of item names to their quantities
     * @return true if order placed successfully, false otherwise
     */
    boolean placeOrder(String tableNumber, Map<String, Integer> items);

    /**
     * Update the status of an item in an order
     * @param tableNumber Table number
     * @param item Item name
     * @param status New status
     * @return true if status updated successfully, false otherwise
     */
    boolean updateOrderStatus(String tableNumber, String item, OrderStatus status);

    /**
     * Get all live orders
     * @return Map of table numbers to their orders (item name to status)
     */
    Map<String, Map<String, OrderStatus>> getAllLiveOrders();

    /**
     * Get orders for a specific table
     * @param tableNumber Table number
     * @return Map of item names to their status
     */
    Map<String, OrderStatus> getOrdersByTable(String tableNumber);

    /**
     * Cancel an order for a table
     * @param tableNumber Table number
     * @return true if cancelled successfully, false otherwise
     */
    boolean cancelOrder(String tableNumber);

    /**
     * Cook a food item for a table
     * @param tableNumber Table number
     * @param itemName Item to be cooked
     * @return Result message about success or missing ingredients
     */
    String cookFoodItem(String tableNumber, String itemName);

    /**
     * Cook all items for a table
     * @param tableNumber Table number
     * @return Map of item names to result messages
     */
    Map<String, String> cookAllItemsForTable(String tableNumber);
}

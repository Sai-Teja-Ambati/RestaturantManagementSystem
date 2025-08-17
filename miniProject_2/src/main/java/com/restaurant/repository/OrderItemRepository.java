package org.restaurant.repository;

import org.restaurant.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all order items for a specific order
    List<OrderItem> findByOrder_OrderId(Long orderId);

    // Find all order items for a specific menu item
    List<OrderItem> findByMenuItem_ItemId(Long menuItemId);

    // Find order items by quantity
    List<OrderItem> findByQuantity(Integer quantity);

    // Find order items by quantity greater than
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);

    // Get total quantity for a specific menu item across all orders
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.menuItem.itemId = :menuItemId")
    Long getTotalQuantityByMenuItem(@Param("menuItemId") Long menuItemId);

    // Get most popular menu items (by total quantity ordered)
    @Query("SELECT oi.menuItem.itemId, oi.menuItem.menuItemName, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "GROUP BY oi.menuItem.itemId, oi.menuItem.menuItemName " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findMostPopularMenuItems();

    // Get order items for orders placed on a specific date
    @Query("SELECT oi FROM OrderItem oi WHERE DATE(oi.order.createdAt) = DATE(:date)")
    List<OrderItem> findOrderItemsByOrderDate(@Param("date") LocalDateTime date);

    // Get total quantity of items ordered between dates
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.order.createdAt BETWEEN :startDate AND :endDate")
    Long getTotalQuantityBetweenDates(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Find order items by menu item category
    @Query("SELECT oi FROM OrderItem oi WHERE oi.menuItem.category = :category")
    List<OrderItem> findByMenuItemCategory(@Param("category") String category);

    // Get revenue by menu item (quantity * price)
    @Query("SELECT oi.menuItem.itemId, oi.menuItem.menuItemName, " +
            "SUM(oi.quantity * oi.menuItem.price) as totalRevenue " +
            "FROM OrderItem oi " +
            "GROUP BY oi.menuItem.itemId, oi.menuItem.menuItemName " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getRevenueByMenuItem();

    // Count distinct orders that contain a specific menu item
    @Query("SELECT COUNT(DISTINCT oi.order.orderId) FROM OrderItem oi WHERE oi.menuItem.itemId = :menuItemId")
    Long countOrdersContainingMenuItem(@Param("menuItemId") Long menuItemId);
}
package org.restaurant.repositories;

import org.restaurant.entities.Order;
import org.restaurant.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTableNumber(Integer tableNumber);

    List<Order> findByWaiter_UserId(Long waiterId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByRestaurantTable_TableId(Long tableId);

    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = DATE(:date)")
    List<Order> findByOrderDate(@Param("date") LocalDateTime date);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt ASC")
    List<Order> findByStatusInOrderByCreatedAtAsc(@Param("statuses") List<OrderStatus> statuses);
}
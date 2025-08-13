package com.restaurant.repository;

import com.restaurant.entity.Order;
import com.restaurant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderId(String orderId);
    
    List<Order> findByCustomer(User customer);
    
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> findByTableNumber(Integer tableNumber);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderTimestamp DESC")
    List<Order> findByCustomerIdOrderByTimestampDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('PENDING', 'CONFIRMED', 'PREPARING') ORDER BY o.orderTimestamp ASC")
    List<Order> findActiveOrders();
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'READY' ORDER BY o.orderTimestamp ASC")
    List<Order> findReadyOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderTimestamp >= :startDate")
    long countOrdersSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(o.billTotal) FROM Order o WHERE o.paymentStatus = 'PAID' AND o.orderTimestamp BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status AND o.orderTimestamp >= :since ORDER BY o.orderTimestamp DESC")
    List<Order> findByStatusSince(@Param("status") Order.OrderStatus status, 
                                 @Param("since") LocalDateTime since);
}
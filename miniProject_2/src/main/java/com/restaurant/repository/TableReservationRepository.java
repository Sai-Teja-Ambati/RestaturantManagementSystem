package com.restaurant.repository;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.TableReservation;
import com.restaurant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long> {
    
    List<TableReservation> findByCustomer(User customer);
    
    List<TableReservation> findByTable(RestaurantTable table);
    
    List<TableReservation> findByStatus(TableReservation.ReservationStatus status);
    
    List<TableReservation> findByTableNumber(Integer tableNumber);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.customer.id = :customerId ORDER BY tr.reservationTime DESC")
    List<TableReservation> findByCustomerIdOrderByReservationTimeDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.status = 'ACTIVE' AND tr.startTime <= :currentTime AND tr.endTime >= :currentTime")
    List<TableReservation> findActiveReservationsAtTime(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.table.id = :tableId AND tr.status = 'ACTIVE' " +
           "AND ((tr.startTime <= :endTime AND tr.endTime >= :startTime))")
    List<TableReservation> findConflictingReservations(@Param("tableId") Long tableId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.startTime BETWEEN :startDate AND :endDate ORDER BY tr.startTime ASC")
    List<TableReservation> findReservationsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(tr) FROM TableReservation tr WHERE tr.status = 'ACTIVE' AND tr.startTime >= :startDate")
    long countActiveReservationsSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.status = 'ACTIVE' AND tr.startTime <= :upcomingTime ORDER BY tr.startTime ASC")
    List<TableReservation> findUpcomingReservations(@Param("upcomingTime") LocalDateTime upcomingTime);
}
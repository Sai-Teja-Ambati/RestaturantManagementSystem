package com.restaurant.repository;

import com.restaurant.entity.TableBooking;
import com.restaurant.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {
    
    List<TableBooking> findByStatus(ReservationStatus status);
    
    List<TableBooking> findByTableNumber(Integer tableNumber);
    
    List<TableBooking> findByCustomerName(String customerName);
    
    List<TableBooking> findByBookingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT tb FROM TableBooking tb WHERE tb.tableNumber = :tableNumber AND tb.status = :status")
    List<TableBooking> findByTableNumberAndStatus(@Param("tableNumber") Integer tableNumber, 
                                                  @Param("status") ReservationStatus status);
    
    @Query("SELECT tb FROM TableBooking tb WHERE tb.bookingTime >= :startTime AND tb.bookingTime <= :endTime AND tb.tableNumber = :tableNumber AND tb.status = 'ACTIVE'")
    List<TableBooking> findConflictingBookings(@Param("tableNumber") Integer tableNumber, 
                                              @Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(tb) FROM TableBooking tb WHERE tb.status = :status")
    long countByStatus(@Param("status") ReservationStatus status);
}
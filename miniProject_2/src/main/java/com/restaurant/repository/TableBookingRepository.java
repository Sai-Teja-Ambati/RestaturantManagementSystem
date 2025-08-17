package org.restaurant.repository;

import org.restaurant.entities.TableBooking;
import org.restaurant.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    List<TableBooking> findByCustomer_UserId(Long customerId);

    List<TableBooking> findByTableNumber(Integer tableNumber);

    List<TableBooking> findByStatus(BookingStatus status);

    @Query("SELECT tb FROM TableBooking tb WHERE DATE(tb.bookingTime) = DATE(:date)")
    List<TableBooking> findByBookingDate(@Param("date") LocalDateTime date);

    @Query("SELECT tb FROM TableBooking tb WHERE tb.tableNumber = :tableNumber AND tb.bookingTime BETWEEN :startTime AND :endTime AND tb.status = :status")
    List<TableBooking> findConflictingBookings(@Param("tableNumber") Integer tableNumber,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("status") BookingStatus status);
}
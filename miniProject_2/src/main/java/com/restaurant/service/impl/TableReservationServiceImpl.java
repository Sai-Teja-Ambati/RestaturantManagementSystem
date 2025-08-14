package com.restaurant.service.impl;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.TableReservation;
import com.restaurant.entity.User;
import com.restaurant.enums.ReservationStatus;
import com.restaurant.repository.TableReservationRepository;
import com.restaurant.service.TableReservationService;
import com.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TableReservationServiceImpl implements TableReservationService {

    @Autowired
    private TableReservationRepository reservationRepository;

    @Autowired
    private TableService tableService;

    @Override
    public TableReservation createReservation(User customer, Integer tableNumber, 
                                            LocalDateTime startTime, LocalDateTime endTime, String notes) {
        
        // Validate time
        if (startTime.isAfter(endTime)) {
            throw new RuntimeException("Start time cannot be after end time");
        }
        
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot make reservation in the past");
        }
        
        // Get table
        RestaurantTable table = tableService.getTableByNumber(tableNumber);
        
        // Check for conflicts
        List<TableReservation> conflicts = reservationRepository.findConflictingReservations(
                table.getId(), startTime, endTime);
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Table " + tableNumber + " is already reserved for the requested time slot");
        }
        
        // Create reservation
        TableReservation reservation = new TableReservation(table, customer, startTime, endTime);
        reservation.setNotes(notes);
        
        return reservationRepository.save(reservation);
    }

    @Override
    public List<TableReservation> getCustomerReservations(Long customerId) {
        return reservationRepository.findByCustomerIdOrderByReservationTimeDesc(customerId);
    }

    @Override
    public List<TableReservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<TableReservation> getActiveReservations() {
        return reservationRepository.findByStatus(ReservationStatus.ACTIVE);
    }

    @Override
    public List<TableReservation> getActiveReservationsAtTime(LocalDateTime time) {
        return reservationRepository.findActiveReservationsAtTime(time);
    }

    @Override
    public TableReservation completeReservation(Long reservationId) {
        TableReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId));
        
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    @Override
    public TableReservation cancelReservation(Long reservationId) {
        TableReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId));
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    @Override
    public List<TableReservation> getReservationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findReservationsBetweenDates(startDate, endDate);
    }

    @Override
    public List<TableReservation> getUpcomingReservations(LocalDateTime upcomingTime) {
        return reservationRepository.findUpcomingReservations(upcomingTime);
    }

    @Override
    public long countActiveReservationsSince(LocalDateTime since) {
        return reservationRepository.countActiveReservationsSince(since);
    }
}
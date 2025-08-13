package com.restaurant.service;

import com.restaurant.entity.TableReservation;
import com.restaurant.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TableReservationService {
    
    TableReservation createReservation(User customer, Integer tableNumber, 
                                     LocalDateTime startTime, LocalDateTime endTime, String notes);
    
    List<TableReservation> getCustomerReservations(Long customerId);
    
    List<TableReservation> getAllReservations();
    
    List<TableReservation> getActiveReservations();
    
    List<TableReservation> getActiveReservationsAtTime(LocalDateTime time);
    
    TableReservation completeReservation(Long reservationId);
    
    TableReservation cancelReservation(Long reservationId);
    
    List<TableReservation> getReservationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    List<TableReservation> getUpcomingReservations(LocalDateTime upcomingTime);
    
    long countActiveReservationsSince(LocalDateTime since);
}
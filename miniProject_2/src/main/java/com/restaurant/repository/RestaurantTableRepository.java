package org.restaurant.repository;

import org.restaurant.entities.RestaurantTable;
import org.restaurant.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    // Find by status
    List<RestaurantTable> findByStatus(TableStatus status);

    // Find by capacity
    List<RestaurantTable> findByCapacity(Integer capacity);

    // Find by minimum capacity
    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer minCapacity);

    // Find by maximum capacity
    List<RestaurantTable> findByCapacityLessThanEqual(Integer maxCapacity);

    // Find available tables with minimum capacity
    @Query("SELECT t FROM RestaurantTable t WHERE t.status = :status AND t.capacity >= :minCapacity")
    List<RestaurantTable> findAvailableTablesWithMinCapacity(@Param("status") TableStatus status,
                                                             @Param("minCapacity") Integer minCapacity);

    // Find table by table number (assuming each table has a unique identifier)
    @Query("SELECT t FROM RestaurantTable t WHERE t.tableId = :tableNumber")
    Optional<RestaurantTable> findByTableNumber(@Param("tableNumber") Long tableNumber);

    // Count tables by status
    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.status = :status")
    Long countByStatus(@Param("status") TableStatus status);

    // Get all available tables ordered by capacity
    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE' ORDER BY t.capacity ASC")
    List<RestaurantTable> findAllAvailableTablesOrderByCapacity();

    // Find smallest available table for given capacity
    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE' AND t.capacity >= :requiredCapacity ORDER BY t.capacity ASC LIMIT 1")
    Optional<RestaurantTable> findSmallestAvailableTable(@Param("requiredCapacity") Integer requiredCapacity);

    // Find tables by capacity range and status
    @Query("SELECT t FROM RestaurantTable t WHERE t.capacity BETWEEN :minCapacity AND :maxCapacity AND t.status = :status")
    List<RestaurantTable> findByCapacityRangeAndStatus(@Param("minCapacity") Integer minCapacity,
                                                       @Param("maxCapacity") Integer maxCapacity,
                                                       @Param("status") TableStatus status);
}
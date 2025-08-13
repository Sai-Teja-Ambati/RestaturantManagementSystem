package zeta.foods.service;

import zeta.foods.model.Order;
import zeta.foods.model.Table;

import java.util.List;
import java.util.Scanner;

/**
 * Service interface for waiter operations in the restaurant
 */
public interface WaiterService {

    /**
     * Take a new order for a customer
     * @param scanner Scanner for user input
     * @return The newly created order
     */
    Order takeNewOrder(Scanner scanner);

    /**
     * Get all tables with their current status
     * @return List of tables with occupation status
     */
    List<Table> getAllTables();

    /**
     * Get all tables that are occupied but not yet served
     * @return List of unserved tables
     */
    List<Table> getUnservedTables();
}

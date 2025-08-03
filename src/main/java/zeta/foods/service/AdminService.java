package zeta.foods.service;

/**
 * Service interface for administrative operations
 */
public interface AdminService {

    /**
     * Fetch the current inventory data
     * @return A string representation of the current inventory
     */
    String fetchCurrentInventory();

    /**
     * Restore the current inventory from backup or default values
     * @return true if restoration was successful, false otherwise
     */
    boolean restoreCurrentInventory();
}

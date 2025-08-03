package zeta.foods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.service.AdminService;
import zeta.foods.utils.CurrentInventory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implementation of AdminService for restaurant management system
 */
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private static final String CURRENT_INVENTORY_FILE_PATH = "src/main/resources/CurrentInventory.txt";

    /**
     * Fetch the current inventory data
     * @return A string representation of the current inventory
     */
    @Override
    public String fetchCurrentInventory() {
        logger.info("Fetching current inventory data");

        try {
            StringBuilder inventoryData = new StringBuilder();

            // Read the file contents
            try (BufferedReader reader = new BufferedReader(new FileReader(CURRENT_INVENTORY_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    inventoryData.append(line).append("\n");
                }
            }

            return inventoryData.toString();

        } catch (IOException e) {
            logger.error("Error fetching current inventory: {}", e.getMessage(), e);
            return "Error fetching inventory data: " + e.getMessage();
        }
    }

    /**
     * Restore the current inventory from backup or default values
     * @return true if restoration was successful, false otherwise
     */
    @Override
    public boolean restoreCurrentInventory() {
        logger.info("Restoring current inventory from main inventory");
        try {
            // Call the private method in CurrentInventory to refresh from main inventory
            CurrentInventory.initializeCurrentInventory();
            return true;
        } catch (Exception e) {
            logger.error("Error restoring current inventory: {}", e.getMessage(), e);
            return false;
        }
    }
}

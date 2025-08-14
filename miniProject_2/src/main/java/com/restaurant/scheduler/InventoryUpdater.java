package com.restaurant.scheduler;

import com.restaurant.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class InventoryUpdater {

    @Autowired
    private InventoryService inventoryService;

    /**
     * Scheduled method to restore inventory from initial inventory file
     * Runs every day at 7:00 AM (07:00)
     * Cron expression: "0 0 7 * * *" means:
     * - 0 seconds
     * - 0 minutes  
     * - 7 hours (7 AM)
     * - every day of month (*)
     * - every month (*)
     * - every day of week (*)
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void restoreInventoryDaily() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        log.info("🔄 Starting scheduled inventory restoration at {}", timestamp);
        
        try {
            // Call the inventory service to restore from file
            inventoryService.restoreInventoryFromFile();
            
            log.info("Scheduled inventory restoration completed successfully at {}", timestamp);
            log.info("Inventory has been restored to initial levels from InitialInventory.txt");
            
        } catch (Exception e) {
            log.error("Scheduled inventory restoration failed at {}: {}", timestamp, e.getMessage(), e);
        }
    }

    /**
     * Method to get the next scheduled execution time
     * Useful for monitoring and debugging the cron schedule
     */
    public String getNextExecutionInfo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextExecution;
        
        // Calculate next 7 AM
        if (now.getHour() < 7) {
            // Today at 7 AM
            nextExecution = now.withHour(7).withMinute(0).withSecond(0).withNano(0);
        } else {
            // Tomorrow at 7 AM
            nextExecution = now.plusDays(1).withHour(7).withMinute(0).withSecond(0).withNano(0);
        }
        
        return "Next inventory restoration scheduled for: " + 
               nextExecution.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
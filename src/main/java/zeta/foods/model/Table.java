package zeta.foods.model;

import java.time.LocalDateTime;

/**
 * Represents a restaurant table
 */
public class Table {
    private int tableNumber;
    private boolean isOccupied;
    private boolean isServed;
    private LocalDateTime bookingStartTime;
    private LocalDateTime bookingEndTime;

    public Table() {
    }

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.isOccupied = false;
        this.isServed = false;
    }

    public Table(int tableNumber, boolean isOccupied, boolean isServed, LocalDateTime bookingStartTime, LocalDateTime bookingEndTime) {
        this.tableNumber = tableNumber;
        this.isOccupied = isOccupied;
        this.isServed = isServed;
        this.bookingStartTime = bookingStartTime;
        this.bookingEndTime = bookingEndTime;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean served) {
        isServed = served;
    }

    public LocalDateTime getBookingStartTime() {
        return bookingStartTime;
    }

    public void setBookingStartTime(LocalDateTime bookingStartTime) {
        this.bookingStartTime = bookingStartTime;
    }

    public LocalDateTime getBookingEndTime() {
        return bookingEndTime;
    }

    public void setBookingEndTime(LocalDateTime bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    @Override
    public String toString() {
        String status = isOccupied ? (isServed ? " (Occupied, Served)" : " (Occupied, Not Served)") : " (Available)";
        return "Table #" + tableNumber + status;
    }
}

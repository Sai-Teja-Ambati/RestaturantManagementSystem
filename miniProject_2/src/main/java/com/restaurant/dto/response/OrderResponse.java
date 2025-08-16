package org.restaurant.dto.response;

import org.restaurant.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private Integer tableNumber;
    private List<String> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String waiterName;
    private Long tableId;

    // Constructors
    public OrderResponse() {}

    public OrderResponse(Long id, Integer tableNumber, List<String> items, OrderStatus status,
                         LocalDateTime createdAt, String waiterName, Long tableId) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
        this.waiterName = waiterName;
        this.tableId = tableId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
}
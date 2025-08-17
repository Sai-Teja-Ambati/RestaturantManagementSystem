package org.restaurant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.restaurant.enums.TableStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    @JsonProperty("tableId")
    private Long tableId;

    @NotNull(message = "Table capacity is required")
    @Min(value = 1, message = "Table capacity must be at least 1")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;

    // Constructors
    public RestaurantTable() {}

    public RestaurantTable(Integer capacity, TableStatus status) {
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RestaurantTable{" +
                "tableId=" + tableId +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }
}
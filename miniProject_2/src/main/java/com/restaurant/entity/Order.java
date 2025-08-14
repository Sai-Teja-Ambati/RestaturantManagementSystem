package com.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"orderTables"})
@ToString(exclude = {"orderTables"})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order ID is required")
    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @JsonIgnoreProperties({"orders", "reservations", "password"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Builder.Default
    @Column(name = "order_timestamp")
    private LocalDateTime orderTimestamp = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "items", columnDefinition = "jsonb")
    private List<Map<String, Object>> items;

    @NotNull(message = "Bill subtotal is required")
    @DecimalMin(value = "0.0", message = "Bill subtotal must be positive")
    @Column(name = "bill_subtotal", precision = 10, scale = 2)
    private BigDecimal billSubtotal;

    @Builder.Default
    @Column(name = "cgst_sgst", precision = 10, scale = 2)
    private BigDecimal cgstSgst = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "service_charge", precision = 10, scale = 2)
    private BigDecimal serviceCharge = BigDecimal.ZERO;

    @Column(name = "bill_total", precision = 10, scale = 2)
    private BigDecimal billTotal;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderTable> orderTables;

    // Custom constructor for creating orders with automatic total calculation
    public Order(String orderId, User customer, List<Map<String, Object>> items, BigDecimal billSubtotal) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.billSubtotal = billSubtotal;
        this.orderTimestamp = LocalDateTime.now();
        this.cgstSgst = BigDecimal.ZERO;
        this.serviceCharge = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        this.orderStatus = OrderStatus.PENDING;
        calculateTotals();
    }

    public void calculateTotals() {
        // Calculate service charge (2%)
        this.serviceCharge = this.billSubtotal.multiply(new BigDecimal("0.02"));
        
        // Add CGST and SGST charges (fixed Rs.25 if bill > 150)
        if (this.billSubtotal.compareTo(new BigDecimal("150")) > 0) {
            this.cgstSgst = new BigDecimal("25.00");
        } else {
            this.cgstSgst = BigDecimal.ZERO;
        }
        
        // Calculate total bill
        this.billTotal = this.billSubtotal.add(this.serviceCharge).add(this.cgstSgst);
    }
}
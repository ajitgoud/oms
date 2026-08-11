package com.blackgoku.oms.order.entity;

import com.blackgoku.oms.order.exception.InvalidOrderStateException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(
                        name = "idx_orders_customer_id",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_orders_status",
                        columnList = "status"
                )
        }
)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "customer_id",
            nullable = false
    )
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal subtotal;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal tax;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal total;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void markInventoryReserved() {

        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Inventory can only be reserved for a PENDING order"
            );
        }

        status = OrderStatus.INVENTORY_RESERVED;
    }

    public void markPaymentPending() {

        if (status != OrderStatus.INVENTORY_RESERVED) {
            throw new InvalidOrderStateException(
                    "Order must have reserved inventory first"
            );
        }

        status = OrderStatus.PAYMENT_PENDING;
    }

    public void markPaymentFailed() {

        if (status != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException(
                    "Payment can only fail for PAYMENT_PENDING order"
            );
        }

        status = OrderStatus.PAYMENT_FAILED;
    }

    public void confirm() {

        if (status != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException(
                    "Only PAYMENT_PENDING orders can be confirmed"
            );
        }

        status = OrderStatus.CONFIRMED;
    }

    public void cancel() {

        if (status != OrderStatus.CONFIRMED &&
                status != OrderStatus.PAYMENT_FAILED) {

            throw new InvalidOrderStateException(
                    "Only CONFIRMED or PAYMENT_FAILED orders can be cancelled"
            );
        }

        status = OrderStatus.CANCELLED;
    }
}
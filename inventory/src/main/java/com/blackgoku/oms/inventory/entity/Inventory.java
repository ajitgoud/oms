package com.blackgoku.oms.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                )
        }
)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;

    @Column(
            name = "total_quantity",
            nullable = false
    )
    private Long totalQuantity;

    @Column(
            name = "reserved_quantity",
            nullable = false
    )
    private Long reservedQuantity;

    @Version
    @Column(nullable = false)
    private Long version;

    @Transient
    public Long getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }
}
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
        name = "inventory_transactions",
        indexes = {
                @Index(
                        name = "idx_inventory_tx_inventory_created",
                        columnList = "inventory_id, created_at"
                )
        }
)
public class InventoryTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "inventory_id",
            nullable = false
    )
    private Long inventoryId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private InventoryTransactionType type;

    @Column(nullable = false)
    private Long quantity;

    @Column(
            nullable = false,
            name = "reference_id"
    )
    private Long referenceId;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reference_type",
            nullable = false,
            length = 50
    )
    private InventoryReferenceType referenceType;
}
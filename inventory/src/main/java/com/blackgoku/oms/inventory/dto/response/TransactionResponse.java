package com.blackgoku.oms.inventory.dto.response;

import com.blackgoku.oms.inventory.entity.InventoryReferenceType;
import com.blackgoku.oms.inventory.entity.InventoryTransactionType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TransactionResponse(

        Long id,

        Long inventoryId,

        InventoryTransactionType type,

        Long quantity,

        Long referenceId,

        InventoryReferenceType referenceType,

        String reason,

        Instant createdAt,

        String createdBy
) {
}
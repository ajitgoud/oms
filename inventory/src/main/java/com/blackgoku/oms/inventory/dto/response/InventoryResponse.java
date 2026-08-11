package com.blackgoku.oms.inventory.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InventoryResponse(

        Long id,

        Long productId,

        Long totalQuantity,

        Long reservedQuantity,

        Long availableQuantity,

        Long version,

        Instant createdAt,

        Instant updatedAt
) {
}
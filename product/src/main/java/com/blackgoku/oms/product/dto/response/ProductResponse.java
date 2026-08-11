package com.blackgoku.oms.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {}

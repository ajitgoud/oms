package com.blackgoku.oms.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductSnapshot(
        Long id,
        BigDecimal price,
        boolean active
) {
}
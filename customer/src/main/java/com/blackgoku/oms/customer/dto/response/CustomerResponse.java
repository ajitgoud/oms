package com.blackgoku.oms.customer.dto.response;

import com.blackgoku.oms.customer.entity.CustomerStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CustomerResponse(
        Long id,
        String customerId,
        String name,
        String email,
        String phone,
        CustomerStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
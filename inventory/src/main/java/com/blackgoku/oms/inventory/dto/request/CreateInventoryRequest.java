package com.blackgoku.oms.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryRequest(

        @NotNull(message = "Product id is required")
        Long productId,

        @NotNull(message = "Total quantity is required")
        @Min(
                value = 0,
                message = "Total quantity cannot be less than zero"
        )
        Long totalQuantity
) {
}
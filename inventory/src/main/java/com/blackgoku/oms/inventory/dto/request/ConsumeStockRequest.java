package com.blackgoku.oms.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConsumeStockRequest(

        @NotNull(message = "Product quantity is required")
        @Min(
                value = 1,
                message = "Quantity must be at least 1"
        )
        Long quantity
) {
}
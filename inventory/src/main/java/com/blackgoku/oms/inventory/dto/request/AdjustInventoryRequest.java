package com.blackgoku.oms.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustInventoryRequest(

        @NotNull(message = "Product quantity is required")
        Long quantity,

        @NotBlank(message = "Reason is required")
        String reason
) {
}
package com.blackgoku.oms.auth.dto.response;

import lombok.Builder;

@Builder
public record RegisterResponse(
        Long id,
        String username,
        String email,
        String role,
        String message
) {
}
package com.blackgoku.oms.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String username,
        String role,
        String token
) {
}
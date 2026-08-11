package com.blackgoku.oms.customer.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private boolean success;

    private String message;

    private List<FieldErrorResponse> errors;

    private Instant timestamp;

    private String path;
}
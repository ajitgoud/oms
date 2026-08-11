package com.blackgoku.oms.payment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {
    private String message;
    private String field;
}
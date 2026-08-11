package com.blackgoku.oms.inventory.dto.response;

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
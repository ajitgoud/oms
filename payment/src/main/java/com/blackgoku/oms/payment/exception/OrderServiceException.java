package com.blackgoku.oms.payment.exception;

import lombok.Getter;

@Getter
public class OrderServiceException extends RuntimeException {

    private final int status;

    public OrderServiceException(
            String message,
            int status
    ) {
        super(message);
        this.status = status;
    }

}
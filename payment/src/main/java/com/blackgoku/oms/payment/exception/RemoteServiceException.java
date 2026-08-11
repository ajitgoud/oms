package com.blackgoku.oms.payment.exception;

public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
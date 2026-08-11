package com.blackgoku.oms.payment.config;

import com.blackgoku.oms.payment.exception.OrderServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (methodKey.contains("OrderClient")) {

            int status = response.status();

            return switch (status) {

                case 404 -> new OrderServiceException("Order not found", status);

                case 409 -> new OrderServiceException("Order cannot transition to the requested payment state", status);

                case 400 -> new OrderServiceException("Invalid request sent to Order Service", status);

                default -> new OrderServiceException("Order Service returned HTTP " + status, status);
            };
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
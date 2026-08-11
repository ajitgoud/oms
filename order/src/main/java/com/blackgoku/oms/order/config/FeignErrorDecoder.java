package com.blackgoku.oms.order.config;

import com.blackgoku.oms.inventory.exception.InsufficientStockException;
import com.blackgoku.oms.inventory.exception.InventoryNotFoundException;
import com.blackgoku.oms.product.exception.ProductNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        int status = response.status();

        if (methodKey.contains("CustomerClient")) {

            if (status == 404) {
                return new CustomerNotFoundException("Customer not found");
            }
        }

        if (methodKey.contains("ProductClient")) {

            if (status == 404) {
                return new ProductNotFoundException("Product not found");
            }
        }

        if (methodKey.contains("InventoryClient")) {

            if (status == 404) {
                return new InventoryNotFoundException("Inventory not found");
            }

            if (status == 409) {
                return new InsufficientStockException("Insufficient stock");
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
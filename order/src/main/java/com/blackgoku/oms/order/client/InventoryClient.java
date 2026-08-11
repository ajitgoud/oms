package com.blackgoku.oms.order.client;

import com.blackgoku.oms.order.config.FeignConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class
)
public interface InventoryClient {

    @PostMapping("/api/v1/inventory/{productId}/reserve")
    void reserve(
            @PathVariable("productId") Long productId,
                 @RequestBody QuantityRequest request
    );

    @PostMapping("/api/v1/inventory/{productId}/release")
    void release(
            @PathVariable("productId") Long productId,
            @RequestBody QuantityRequest request
    );

    record QuantityRequest(Long quantity) {

    }

}
package com.blackgoku.oms.order.client;

import com.blackgoku.oms.order.config.FeignConfig;
import com.blackgoku.oms.order.dto.response.ProductSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}/snapshot")
    ProductSnapshot getSnapshot(@PathVariable("id") Long productId);
}
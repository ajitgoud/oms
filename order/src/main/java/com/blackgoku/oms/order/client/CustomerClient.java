package com.blackgoku.oms.order.client;

import com.blackgoku.oms.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "customer-service",
        configuration = FeignConfig.class
)
public interface CustomerClient {
    @GetMapping("api/v1/customers/{id}")
    void validateCustomer(@PathVariable("id") Long customerId);
}
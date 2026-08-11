package com.blackgoku.oms.payment.client;


import com.blackgoku.oms.payment.config.FeignConfig;
import com.blackgoku.oms.payment.dto.response.OrderPaymentInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "order-service",
        configuration = FeignConfig.class
)
public interface OrderClient {

    @GetMapping("/api/v1/orders/{id}/payment-info")
    OrderPaymentInfoResponse getOrder(Long orderId);

    @GetMapping("/api/v1/orders/{id}/payment-pending")
    void markPaymentPending(Long orderId);

    @GetMapping("/api/v1/orders/{id}/confirm-payment")
    void confirmPayment(Long orderId);

    @GetMapping("/api/v1/orders/{id}/payment-failed")
    void markPaymentFailed(Long orderId);
}
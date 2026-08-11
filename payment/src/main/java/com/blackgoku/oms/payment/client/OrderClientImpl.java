package com.blackgoku.oms.payment.client;

import com.blackgoku.oms.payment.dto.response.OrderPaymentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OrderClientImpl implements OrderClient {

    private final RestClient orderRestClient;

    @Override
    public OrderPaymentInfoResponse getOrder(Long orderId) {

        return orderRestClient
                .get()
                .uri("/api/v1/orders/{id}/payment-info", orderId)
                .retrieve()
                .body(OrderPaymentInfoResponse.class);
    }

    @Override
    public void markPaymentPending(Long orderId) {

        orderRestClient
                .post()
                .uri("/api/v1/orders/{id}/payment-pending", orderId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void confirmPayment(Long orderId) {

        orderRestClient
                .post()
                .uri("/api/v1/orders/{id}/confirm-payment", orderId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void markPaymentFailed(Long orderId) {

        orderRestClient
                .post()
                .uri("/api/v1/orders/{id}/payment-failed", orderId)
                .retrieve()
                .toBodilessEntity();
    }
}
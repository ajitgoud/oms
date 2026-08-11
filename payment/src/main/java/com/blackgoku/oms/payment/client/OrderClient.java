package com.blackgoku.oms.payment.client;


import com.blackgoku.oms.payment.dto.response.OrderPaymentInfoResponse;

public interface OrderClient {

    OrderPaymentInfoResponse getOrder(Long orderId);

    void markPaymentPending(Long orderId);

    void confirmPayment(Long orderId);

    void markPaymentFailed(Long orderId);
}
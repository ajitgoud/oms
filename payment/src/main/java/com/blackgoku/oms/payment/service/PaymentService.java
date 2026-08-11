package com.blackgoku.oms.payment.service;

import com.blackgoku.oms.payment.dto.request.CreatePaymentRequest;
import com.blackgoku.oms.payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse create(
            CreatePaymentRequest request,
            String idempotencyKey
    );

    PaymentResponse process(Long paymentId);
}
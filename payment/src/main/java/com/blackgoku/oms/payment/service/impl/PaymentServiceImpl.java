package com.blackgoku.oms.payment.service.impl;

import com.blackgoku.oms.payment.client.OrderClient;
import com.blackgoku.oms.payment.dto.request.CreatePaymentRequest;
import com.blackgoku.oms.payment.dto.response.OrderPaymentInfoResponse;
import com.blackgoku.oms.payment.dto.response.OrderPaymentStatus;
import com.blackgoku.oms.payment.dto.response.PaymentResponse;
import com.blackgoku.oms.payment.entity.Payment;
import com.blackgoku.oms.payment.entity.PaymentAttempt;
import com.blackgoku.oms.payment.entity.PaymentAttemptStatus;
import com.blackgoku.oms.payment.entity.PaymentStatus;
import com.blackgoku.oms.payment.exception.*;
import com.blackgoku.oms.payment.gateway.PaymentGateway;
import com.blackgoku.oms.payment.gateway.PaymentGatewayResult;
import com.blackgoku.oms.payment.gateway.PaymentGatewayStatus;
import com.blackgoku.oms.payment.repository.PaymentAttemptRepository;
import com.blackgoku.oms.payment.repository.PaymentRepository;
import com.blackgoku.oms.payment.service.PaymentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final OrderClient orderClient;
    private final PaymentGateway paymentGateway;

    @Transactional
    @Override
    public PaymentResponse create(
            CreatePaymentRequest request,
            String idempotencyKey
    ) {

        String requestHash = generateRequestHash(request);

        var existingAttempt = paymentAttemptRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existingAttempt.isPresent()) {

            PaymentAttempt attempt = existingAttempt.get();

            if (!attempt.getRequestHash().equals(requestHash)) {
                throw new IdempotencyKeyConflictException(
                        "Idempotency key was already used with a different request"
                );
            }

            Payment payment = paymentRepository
                    .findById(attempt.getPaymentId())
                    .orElseThrow(() ->
                            new PaymentNotFoundException(
                                    "Payment associated with idempotency key not found"
                            )
                    );

            return toResponse(payment);
        }

        OrderPaymentInfoResponse order;

        try {
            order = orderClient.getOrder(request.orderId());
        } catch (FeignException ex) {
            throw new RemoteServiceException(
                    "Order Service is unavailable",
                    ex
            );
        }

        if (order == null) {
            throw new PaymentNotFoundException(
                    "Order not found: " + request.orderId()
            );
        }

        if (order.status() != OrderPaymentStatus.INVENTORY_RESERVED) {
            throw new InvalidPaymentStateException(
                    "Order is not ready for payment"
            );
        }

        if (paymentRepository.existsByOrderId(request.orderId())) {
            throw new PaymentAlreadyExistsException(
                    "Payment already exists for order: "
                            + request.orderId()
            );
        }

        Payment payment = Payment.builder()
                .orderId(order.id())
                .amount(order.total())
                .method(request.method())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentAttempt attempt = PaymentAttempt.builder()
                .paymentId(savedPayment.getId())
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .status(PaymentAttemptStatus.SUCCESS)
                .build();

        paymentAttemptRepository.save(attempt);

        try {
            orderClient.markPaymentPending(order.id());
        } catch (FeignException ex) {
            throw new RemoteServiceException(
                    "Order Service is unavailable",
                    ex
            );
        }

        return toResponse(savedPayment);
    }

    @Transactional
    @Override
    public PaymentResponse process(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found: " + paymentId
                        )
                );

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment is not in PENDING state"
            );
        }

        payment.setStatus(PaymentStatus.PROCESSING);

        PaymentGatewayResult result =
                paymentGateway.charge(payment);

        if (result.status() == PaymentGatewayStatus.SUCCESS) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setGatewayTransactionId(
                    result.transactionId()
            );

            try {
                orderClient.confirmPayment(payment.getOrderId());
            } catch (FeignException ex) {
                throw new RemoteServiceException(
                        "Order Service is unavailable",
                        ex
                );
            }

        } else {

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(
                    result.failureReason()
            );

            try {
                orderClient.markPaymentFailed(payment.getOrderId());
            } catch (FeignException ex) {
                throw new RemoteServiceException(
                        "Order Service is unavailable",
                        ex
                );
            }
        }

        return toResponse(payment);
    }

    private String generateRequestHash(
            CreatePaymentRequest request
    ) {

        String value =
                request.orderId() + ":" + request.method().name();

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(
                        String.format("%02x", b)
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Unable to generate request hash",
                    e
            );
        }
    }

    private PaymentResponse toResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .gatewayTransactionId(
                        payment.getGatewayTransactionId()
                )
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
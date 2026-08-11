package com.blackgoku.oms.payment.controller;

import com.blackgoku.oms.payment.dto.request.CreatePaymentRequest;
import com.blackgoku.oms.payment.dto.response.PaymentResponse;
import com.blackgoku.oms.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        paymentService.create(
                                request,
                                idempotencyKey
                        )
                );
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> process(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                paymentService.process(id)
        );
    }
}
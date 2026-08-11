package com.blackgoku.oms.order.controller;

import com.blackgoku.oms.order.dto.request.CreateOrderRequest;
import com.blackgoku.oms.order.dto.response.OrderResponse;
import com.blackgoku.oms.order.dto.response.OrderSummaryResponse;
import com.blackgoku.oms.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(orderService.getOrders(page, size));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {

        return ResponseEntity.ok(orderService.cancel(id));
    }
}
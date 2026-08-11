package com.blackgoku.oms.order.service;

import com.blackgoku.oms.order.dto.request.CreateOrderRequest;
import com.blackgoku.oms.order.dto.response.OrderResponse;
import com.blackgoku.oms.order.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);

    OrderResponse getById(Long id);

    Page<OrderSummaryResponse> getOrders(int page, int size);

    OrderResponse cancel(Long orderId);

    void confirmPayment(Long orderId);

    void markPaymentFailed(Long orderId);
}
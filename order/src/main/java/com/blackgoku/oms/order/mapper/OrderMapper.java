package com.blackgoku.oms.order.mapper;

import com.blackgoku.oms.order.dto.response.*;
import com.blackgoku.oms.order.entity.Order;
import com.blackgoku.oms.order.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .total(order.getTotal())
                .version(order.getVersion())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(
                        order.getItems()
                                .stream()
                                .map(this::toItemResponse)
                                .toList()
                )
                .build();
    }

    public OrderSummaryResponse toSummaryResponse(
            Order order
    ) {

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(
            OrderItem item
    ) {

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
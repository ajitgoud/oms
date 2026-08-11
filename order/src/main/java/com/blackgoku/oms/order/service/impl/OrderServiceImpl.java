package com.blackgoku.oms.order.service.impl;

import com.blackgoku.oms.order.client.CustomerClient;
import com.blackgoku.oms.order.client.InventoryClient;
import com.blackgoku.oms.order.client.ProductClient;
import com.blackgoku.oms.order.dto.request.CreateOrderItemRequest;
import com.blackgoku.oms.order.dto.request.CreateOrderRequest;
import com.blackgoku.oms.order.dto.response.OrderResponse;
import com.blackgoku.oms.order.dto.response.OrderSummaryResponse;
import com.blackgoku.oms.order.dto.response.ProductSnapshot;
import com.blackgoku.oms.order.entity.Order;
import com.blackgoku.oms.order.entity.OrderItem;
import com.blackgoku.oms.order.entity.OrderStatus;
import com.blackgoku.oms.order.exception.InvalidOrderStateException;
import com.blackgoku.oms.order.exception.OrderNotFoundException;
import com.blackgoku.oms.order.mapper.OrderMapper;
import com.blackgoku.oms.order.repository.OrderRepository;
import com.blackgoku.oms.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderResponse create(CreateOrderRequest request) {

        customerClient.validateCustomer(request.customerId());

        validateDuplicateProducts(request);

        Order order = Order.builder()
                .customerId(request.customerId())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {

            ProductSnapshot product = productClient.getSnapshot(itemRequest.productId());

            if (!product.active()) {
                throw new InvalidOrderStateException("Product is inactive: " + product.id());
            }

            BigDecimal itemSubtotal = product.price().multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem item = OrderItem.builder()
                    .productId(product.id())
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.price())
                    .subtotal(itemSubtotal)
                    .build();

            order.addItem(item);

            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal tax = subtotal.multiply(TAX_RATE);

        BigDecimal total = subtotal.add(tax);

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> reservedItems = new ArrayList<>();

        try {

            for (OrderItem item : savedOrder.getItems()) {
                inventoryClient.reserve(item.getProductId(), item.getQuantity(), savedOrder.getId());
                reservedItems.add(item);
            }

        } catch (RuntimeException ex) {
            compensateInventory(savedOrder.getId(), reservedItems);
            throw ex;
        }

        savedOrder.markInventoryReserved();
        savedOrder.markPaymentPending();

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getById(Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderSummaryResponse> getOrders(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        return orderRepository.findAll(PageRequest.of(page, size)).map(orderMapper::toSummaryResponse);
    }

    @Transactional
    @Override
    public OrderResponse cancel(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {

            throw new InvalidOrderStateException("Payment is still pending");
        }

        if (order.getStatus() == OrderStatus.INVENTORY_RESERVED) {

            releaseInventory(order);

            throw new InvalidOrderStateException("Order was not ready for cancellation");
        }

        order.cancel();

        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public void confirmPayment(Long orderId) {

        Order order = findOrder(orderId);

        order.confirm();
    }

    @Transactional
    @Override
    public void markPaymentFailed(Long orderId) {

        Order order = findOrder(orderId);

        if (order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            return;
        }

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            throw new InvalidOrderStateException("Order is not in PAYMENT_PENDING state");
        }

        releaseInventory(order);

        order.markPaymentFailed();
    }

    private void validateDuplicateProducts(CreateOrderRequest request) {

        Set<Long> productIds = new HashSet<>();

        for (CreateOrderItemRequest item : request.items()) {

            if (!productIds.add(item.productId())) {

                throw new InvalidOrderStateException("Duplicate product id in order: " + item.productId());
            }
        }
    }

    private void releaseInventory(Order order) {

        for (OrderItem item : order.getItems()) {

            inventoryClient.release(item.getProductId(), item.getQuantity(), order.getId());
        }
    }

    private void compensateInventory(Long orderId, List<OrderItem> reservedItems) {

        for (OrderItem item : reservedItems) {

            try {

                inventoryClient.release(item.getProductId(), item.getQuantity(), orderId);

            } catch (Exception compensationException) {

                /*
                 * Compensation failure is serious.
                 * The order transaction will roll back,
                 * but inventory may remain reserved.
                 *
                 * This is one of the reasons distributed
                 * workflows eventually need a Saga.
                 */
            }
        }
    }

    private Order findOrder(Long orderId) {

        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }
}
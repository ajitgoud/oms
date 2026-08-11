package com.blackgoku.oms.order.repository;

import com.blackgoku.oms.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
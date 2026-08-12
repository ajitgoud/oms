package com.blackgoku.oms.order.repository;

import com.blackgoku.oms.order.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByEventIdAndConsumer(UUID eventId, String consumer);
}

package com.blackgoku.oms.payment.repository;

import com.blackgoku.oms.payment.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByEventIdAndConsumer(UUID eventId, String consumer);
}

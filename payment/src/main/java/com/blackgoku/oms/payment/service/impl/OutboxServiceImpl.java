package com.blackgoku.oms.payment.service.impl;

import com.blackgoku.oms.payment.entity.OutboxEvent;
import com.blackgoku.oms.payment.event.EventEnvelope;
import com.blackgoku.oms.payment.repository.OutboxEventRepository;
import com.blackgoku.oms.payment.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(EventEnvelope<?> event) {

        try {

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.eventId())
                    .eventType(event.eventType())
                    .aggregateType(event.aggregateType())
                    .aggregateId(event.aggregateId())
                    .payload(objectMapper.writeValueAsString(event))
                    .published(false)
                    .build();

            repository.save(outboxEvent);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create outbox event", ex);
        }
    }
}
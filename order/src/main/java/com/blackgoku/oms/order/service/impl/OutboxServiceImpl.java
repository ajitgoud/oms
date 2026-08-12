package com.blackgoku.oms.order.service.impl;

import com.blackgoku.oms.order.kafka.EventEnvelope;
import com.blackgoku.oms.order.outbox.OutboxEvent;
import com.blackgoku.oms.order.repository.OutboxEventRepository;
import com.blackgoku.oms.order.service.OutboxService;
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
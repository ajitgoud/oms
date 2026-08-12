package com.blackgoku.oms.payment.event;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        String aggregateType,
        Long aggregateId,
        Instant occurredAt,
        T payload
) {
}
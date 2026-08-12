package com.blackgoku.oms.order.service;

import com.blackgoku.oms.order.kafka.EventEnvelope;

public interface OutboxService {
    void save(EventEnvelope<?> event);
}
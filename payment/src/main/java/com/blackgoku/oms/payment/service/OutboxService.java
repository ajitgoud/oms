package com.blackgoku.oms.payment.service;


import com.blackgoku.oms.payment.event.EventEnvelope;

public interface OutboxService {
    void save(EventEnvelope<?> event);
}
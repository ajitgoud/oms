package com.blackgoku.oms.payment.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {
    private static final String TOPIC = "payment-events";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<?> publish(String key, String payload) {
       return kafkaTemplate.send(TOPIC, key, payload);
    }
}

package com.blackgoku.oms.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private static final String TOPIC = "order-events";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<?> publish(String key, String payload) {
       return kafkaTemplate.send(TOPIC, key, payload);
    }
}

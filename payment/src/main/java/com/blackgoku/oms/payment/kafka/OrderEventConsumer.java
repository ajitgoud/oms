package com.blackgoku.oms.payment.kafka;

import com.blackgoku.oms.payment.entity.ProcessedEvent;
import com.blackgoku.oms.payment.event.EventEnvelope;
import com.blackgoku.oms.payment.event.OrderCreatedEvent;
import com.blackgoku.oms.payment.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private static final String CONSUMER = "payment-service";
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository repository;

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    @Transactional
    public void consume(String payload, @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        try {
            EventEnvelope<OrderCreatedEvent> envelope =
                    objectMapper.readValue(
                            payload,
                            new TypeReference<EventEnvelope<OrderCreatedEvent>>() {
                            }
                    );
            if (repository.existsByEventIdAndConsumer(envelope.eventId(), CONSUMER)) {
                log.info("Event already processed: {}", envelope.eventId());
                return;
            }
            OrderCreatedEvent event = envelope.payload();
            log.info("Received OrderCreatedEvent: orderId={}, customerId={}, total={}, key={}",
                    event.orderId(), event.customerId(), event.total(), key);

            //Payment business logic

            repository.save(ProcessedEvent.builder()
                    .eventId(envelope.eventId())
                    .consumer(CONSUMER)
                    .processedAt(Instant.now())
                    .build()
            );


        } catch (Exception ex) {
            log.error("Failed to deserialize OrderCreatedEvent: payload={}", payload, ex);
            throw new IllegalStateException("Failed to process OrderCreatedEvent", ex);
        }
    }
}
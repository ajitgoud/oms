package com.blackgoku.oms.order.kafka;

import com.blackgoku.oms.order.entity.EventType;
import com.blackgoku.oms.order.entity.ProcessedEvent;
import com.blackgoku.oms.order.repository.ProcessedEventRepository;
import com.blackgoku.oms.order.service.OrderService;
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
public class PaymentEventConsumer {
    private static final String CONSUMER = "order-service";
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository repository;
    private final OrderService service;

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    @Transactional
    public void consume(String payload, @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        try {
            EventEnvelope<Object> envelope =
                    objectMapper.readValue(
                            payload,
                            new TypeReference<EventEnvelope<Object>>() {
                            }
                    );
            if (repository.existsByEventIdAndConsumer(envelope.eventId(), CONSUMER)) {
                log.info("Event already processed: {}", envelope.eventId());
                return;
            }
            switch (EventType.valueOf(envelope.eventType())){
                case EventType.PAYMENT_SUCCESS-> {
                    PaymentSuccessEvent event = objectMapper.convertValue(envelope.payload(), PaymentSuccessEvent.class);
                    service.confirmPayment(event.orderId());
                }
                case EventType.PAYMENT_FAILED -> {
                    PaymentFailedEvent event = objectMapper.convertValue(envelope.payload(), PaymentFailedEvent.class);
                    service.markPaymentFailed(event.orderId());
                }

                default -> {
                    log.warn("Ignoring unsupported payment event: {}", envelope.eventType());
                    return;
                }
            }

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
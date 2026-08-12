package com.blackgoku.oms.payment.service.impl;

import com.blackgoku.oms.payment.entity.OutboxEvent;
import com.blackgoku.oms.payment.kafka.PaymentEventProducer;
import com.blackgoku.oms.payment.repository.OutboxEventRepository;
import com.blackgoku.oms.payment.service.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherImpl implements OutboxPublisher {
    private final OutboxEventRepository repository;
    private final PaymentEventProducer eventProducer;

    @Scheduled(fixedDelay = 5 * 1000)
    @Transactional
    @Override
    public void publish() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "createdAt"));

        List<OutboxEvent> events = repository.findByPublishedFalseOrderByCreatedAtAsc(pageable);
        for (OutboxEvent event : events) {
            try{
                eventProducer.publish(event.getAggregateId().toString(), event.getPayload()).join();
                event.setPublished(true);
                event.setPublishedAt(Instant.now());
                log.info("Published outbox event: id={}, type={}, aggregateId={}", event.getEventId(), event.getEventType(), event.getAggregateId());

            }catch (Exception ex){
                log.error("Failed to publish outbox event: id={}, type={}", event.getEventId(), event.getEventType(), ex);
            }
        }
    }
}

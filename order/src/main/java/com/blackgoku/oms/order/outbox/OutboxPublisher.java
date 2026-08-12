package com.blackgoku.oms.order.outbox;

import com.blackgoku.oms.order.kafka.OrderEventProducer;
import com.blackgoku.oms.order.repository.OutboxEventRepository;
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
public class OutboxPublisher {
    private final OutboxEventRepository repository;
    private final OrderEventProducer eventProducer;

    @Scheduled(fixedDelay = 5 * 1000)
    @Transactional
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

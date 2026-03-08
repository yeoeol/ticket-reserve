package ticket.reserve.core.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ticket.reserve.core.log.DataSerializer;
import ticket.reserve.core.log.context.TraceContextManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

    private final TraceContextManager traceContextManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        log.info("[MessageRelay.createOutbox] outboxEvent={}", outboxEvent);

        Map<String, String> carrier = traceContextManager.captureCurrentContext();

        Outbox outbox = outboxEvent.getOutbox();
        outbox.setTraceContext(DataSerializer.serialize(carrier));
        outboxRepository.save(outboxEvent.getOutbox());
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent) {
        publishEvent(outboxEvent.getOutbox());
    }

    private void publishEvent(Outbox outbox) {
        Map<String, String> carrier = Collections.emptyMap();
        try {
            String serializedTraceContext = outbox.getTraceContext();
            if (serializedTraceContext != null && !serializedTraceContext.isBlank()) {
                carrier = DataSerializer.deserialize(serializedTraceContext, Map.class);
            }
        } catch (Exception e) {
            log.warn("[MessageRelay.publishEvent] traceContext restore failed. outboxId={}", outbox.getOutboxId(), e);
        }

        traceContextManager.runWithContext(carrier, "message-relay-publish", () -> {
            try {
                messageRelayKafkaTemplate.send(
                        outbox.getEventType().getTopic(),
                        String.valueOf(outbox.getPartitionKey()),
                        outbox.getPayload()
                ).get(1, TimeUnit.SECONDS);
                outboxRepository.delete(outbox);
            } catch (Exception e) {
                log.error("[MessageRelay.publishEvent] outbox={}", outbox, e);
            }
        });
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS
    )
    public void publishPendingEvent() {
        List<Outbox> outboxes = outboxRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(
            LocalDateTime.now().minusSeconds(10),
            Pageable.ofSize(100)
        );
        if (outboxes.isEmpty()) {
            return;
        }

        log.info("[MessageRelay] Found {} pending events", outboxes.size());
        for (Outbox outbox : outboxes) {
            publishEvent(outbox);
        }
    }
}

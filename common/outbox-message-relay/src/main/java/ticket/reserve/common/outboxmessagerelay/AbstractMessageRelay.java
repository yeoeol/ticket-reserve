package ticket.reserve.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        log.info("[MessageRelay.createOutbox outboxEvent={}", outboxEvent);
        outboxRepository.save(outboxEvent.getOutbox());
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent) {
        publishEvent(outboxEvent.getOutbox());
    }

    // ShedLock이 적용될 스케줄러에서 호출할 핵심 로직
    protected void runRelayProcess() {
        List<Outbox> outboxes = outboxRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(
                LocalDateTime.now().minusSeconds(10),
                Pageable.ofSize(100)
        );

        if (outboxes.isEmpty()) return;

        log.info("[MessageRelay] Found {} pending events", outboxes.size());
        for (Outbox outbox : outboxes) {
            publishEvent(outbox);
        }
    }

    private void publishEvent(Outbox outbox) {
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
    }
}

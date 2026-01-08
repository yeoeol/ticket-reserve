package ticket.reserve.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventPayload;
import ticket.reserve.common.event.EventType;
import ticket.reserve.tsid.IdGenerator;


@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdGenerator idGenerator;

    public void publish(EventType type, EventPayload payload, Long partitionKey) {
        log.info("[OutboxEventPublisher.publish] EventType={}, EventPayload={}",
                type, payload.getClass());

        long outboxId = idGenerator.nextId();
        long eventId = idGenerator.nextId();

        Outbox outbox = Outbox.create(
            outboxId,
            type,
            Event.of(
                eventId, type, payload
            ).toJson(),
            partitionKey
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }

}

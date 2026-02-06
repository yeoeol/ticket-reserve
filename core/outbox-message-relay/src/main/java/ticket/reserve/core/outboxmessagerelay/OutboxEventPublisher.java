package ticket.reserve.core.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.tsid.IdGenerator;


@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdGenerator idGenerator;

    public void publish(EventType type, EventPayload payload, Long partitionKey) {
        log.info("[OutboxEventPublisher.publish] EventType={}, EventPayload={}, EventPayload.getClass={}",
                type, payload, payload.getClass());

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

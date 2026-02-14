package ticket.reserve.hotbusking.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCreatedEventPayload;
import ticket.reserve.hotbusking.global.util.TimeCalculatorUtils;
import ticket.reserve.hotbusking.infrastructure.persistence.redis.BuskingSubscriptionCountRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCreatedEventHandler implements EventHandler<SubscriptionCreatedEventPayload> {

    private final BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;

    @Override
    public void handle(Event<SubscriptionCreatedEventPayload> event) {
        SubscriptionCreatedEventPayload payload = event.getPayload();
        buskingSubscriptionCountRepository.createOrUpdate(
                payload.getBuskingId(),
                payload.getBuskingSubscriptionCount(),
                TimeCalculatorUtils.calculateDurationToStartTime(payload.getStartTime())
        );
    }

    @Override
    public boolean supports(Event<SubscriptionCreatedEventPayload> event) {
        return EventType.SUBSCRIPTION_CREATED == event.getType();
    }

    @Override
    public Long findBuskingId(Event<SubscriptionCreatedEventPayload> event) {
        return event.getPayload().getBuskingId();
    }

    @Override
    public LocalDateTime findStartTime(Event<SubscriptionCreatedEventPayload> event) {
        return event.getPayload().getStartTime();
    }
}

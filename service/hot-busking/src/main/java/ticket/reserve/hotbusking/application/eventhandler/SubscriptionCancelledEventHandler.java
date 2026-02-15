package ticket.reserve.hotbusking.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCancelledEventPayload;
import ticket.reserve.hotbusking.global.util.TimeCalculatorUtils;
import ticket.reserve.hotbusking.infrastructure.persistence.redis.BuskingSubscriptionCountRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCancelledEventHandler implements EventHandler<SubscriptionCancelledEventPayload> {

    private final BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;

    @Override
    public void handle(Event<SubscriptionCancelledEventPayload> event) {
        SubscriptionCancelledEventPayload payload = event.getPayload();
        buskingSubscriptionCountRepository.createOrUpdate(
                payload.getBuskingId(),
                payload.getBuskingSubscriptionCount(),
                TimeCalculatorUtils.calculateDurationToStartTime(payload.getStartTime())
        );
    }

    @Override
    public boolean supports(Event<SubscriptionCancelledEventPayload> event) {
        return EventType.SUBSCRIPTION_CANCELLED == event.getType();
    }

    @Override
    public Long findBuskingId(Event<SubscriptionCancelledEventPayload> event) {
        return event.getPayload().getBuskingId();
    }

    @Override
    public LocalDateTime findStartTime(Event<SubscriptionCancelledEventPayload> event) {
        return event.getPayload().getStartTime();
    }
}

package ticket.reserve.subscription.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCreatedEventPayload;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;
import ticket.reserve.subscription.util.TimeCalculatorUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCreatedEventHandler implements EventHandler<SubscriptionCreatedEventPayload> {

    private final BuskingInfoPort buskingInfoPort;

    @Override
    public void handle(Event<SubscriptionCreatedEventPayload> event) {
        SubscriptionCreatedEventPayload payload = event.getPayload();
        buskingInfoPort.createOrUpdate(
                payload.getBuskingId(),
                payload.getTitle(),
                payload.getLocation(),
                payload.getStartTime(),
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

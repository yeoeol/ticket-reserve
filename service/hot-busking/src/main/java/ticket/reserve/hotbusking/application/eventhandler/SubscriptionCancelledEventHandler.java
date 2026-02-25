package ticket.reserve.hotbusking.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCancelledEventPayload;
import ticket.reserve.hotbusking.application.port.out.BuskingSubscriptionCountPort;
import ticket.reserve.hotbusking.global.util.TimeCalculatorUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCancelledEventHandler implements EventHandler<SubscriptionCancelledEventPayload> {

    private final BuskingSubscriptionCountPort buskingSubscriptionCountPort;

    @Override
    public void handle(Event<SubscriptionCancelledEventPayload> event) {
        SubscriptionCancelledEventPayload payload = event.getPayload();
        buskingSubscriptionCountPort.createOrUpdate(
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

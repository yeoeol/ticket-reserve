package ticket.reserve.subscription.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingUpdatedEventPayload;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BuskingUpdatedEventHandler implements EventHandler<BuskingUpdatedEventPayload> {

    private final BuskingInfoPort buskingInfoPort;

    @Override
    public void handle(Event<BuskingUpdatedEventPayload> event) {
        BuskingUpdatedEventPayload payload = event.getPayload();
        buskingInfoPort.createOrUpdate(
                payload.getBuskingId(),
                payload.getTitle(),
                payload.getLocation(),
                payload.getStartTime()
        );
    }

    @Override
    public boolean supports(Event<BuskingUpdatedEventPayload> event) {
        return EventType.BUSKING_UPDATED == event.getType();
    }

    @Override
    public Long findBuskingId(Event<BuskingUpdatedEventPayload> event) {
        return event.getPayload().getBuskingId();
    }

    @Override
    public LocalDateTime findStartTime(Event<BuskingUpdatedEventPayload> event) {
        return event.getPayload().getStartTime();
    }
}

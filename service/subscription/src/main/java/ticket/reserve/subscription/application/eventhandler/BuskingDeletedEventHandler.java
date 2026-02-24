package ticket.reserve.subscription.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.event.payload.BuskingDeletedEventPayload;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BuskingDeletedEventHandler implements EventHandler<BuskingDeletedEventPayload> {

    private final BuskingInfoPort buskingInfoPort;

    @Override
    public void handle(Event<BuskingDeletedEventPayload> event) {
        BuskingDeletedEventPayload payload = event.getPayload();
        buskingInfoPort.delete(payload.getBuskingId());
    }

    @Override
    public boolean supports(Event<BuskingDeletedEventPayload> event) {
        return EventType.BUSKING_DELETED == event.getType();
    }

    @Override
    public Long findBuskingId(Event<BuskingDeletedEventPayload> event) {
        return event.getPayload().getBuskingId();
    }

    @Override
    public LocalDateTime findStartTime(Event<BuskingDeletedEventPayload> event) {
        return event.getPayload().getStartTime();
    }
}

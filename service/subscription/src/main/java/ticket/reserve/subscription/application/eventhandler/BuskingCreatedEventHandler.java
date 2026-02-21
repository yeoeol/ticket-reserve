package ticket.reserve.subscription.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final BuskingInfoPort buskingInfoPort;

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();
        buskingInfoPort.createOrUpdate(
                payload.getBuskingId(),
                payload.getTitle(),
                payload.getLocation(),
                payload.getStartTime()
        );
    }

    @Override
    public boolean supports(Event<BuskingCreatedEventPayload> event) {
        return EventType.BUSKING_CREATED == event.getType();
    }

    @Override
    public Long findBuskingId(Event<BuskingCreatedEventPayload> event) {
        return event.getPayload().getBuskingId();
    }

    @Override
    public LocalDateTime findStartTime(Event<BuskingCreatedEventPayload> event) {
        return event.getPayload().getStartTime();
    }
}

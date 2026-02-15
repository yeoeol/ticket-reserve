package ticket.reserve.hotbusking.application.eventhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.hotbusking.global.util.TimeCalculatorUtils;
import ticket.reserve.hotbusking.infrastructure.persistence.redis.HotBuskingListRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final HotBuskingListRepository hotBuskingListRepository;

    private static final long HOT_BUSKING_COUNT = 5;

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();
        hotBuskingListRepository.add(
                payload.getBuskingId(),
                0L,
                HOT_BUSKING_COUNT,
                TimeCalculatorUtils.calculateDurationToStartTime(payload.getStartTime())
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

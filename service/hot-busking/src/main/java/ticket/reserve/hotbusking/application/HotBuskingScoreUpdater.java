package ticket.reserve.hotbusking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.hotbusking.application.eventhandler.EventHandler;
import ticket.reserve.hotbusking.application.port.out.HotBuskingListPort;
import ticket.reserve.hotbusking.global.util.TimeCalculatorUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HotBuskingScoreUpdater {

    private final HotBuskingListPort hotBuskingListPort;
    private final HotBuskingScoreCalculator hotBuskingScoreCalculator;

    private static final long HOT_BUSKING_COUNT = 5;

    public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {
        Long buskingId = eventHandler.findBuskingId(event);
        LocalDateTime startTime = eventHandler.findStartTime(event);
        Duration ttl = TimeCalculatorUtils.calculateDurationToStartTime(startTime);

        if (ttl.isZero()) {
            return;
        }

        eventHandler.handle(event);

        long score = hotBuskingScoreCalculator.calculate(buskingId);
        hotBuskingListPort.add(
                buskingId,
                score,
                HOT_BUSKING_COUNT,
                ttl
        );
    }
}

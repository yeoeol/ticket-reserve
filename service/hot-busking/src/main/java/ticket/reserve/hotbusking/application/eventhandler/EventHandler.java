package ticket.reserve.hotbusking.application.eventhandler;

import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;

import java.time.LocalDateTime;

public interface EventHandler <T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findBuskingId(Event<T> event);
    LocalDateTime findStartTime(Event<T> event);
}

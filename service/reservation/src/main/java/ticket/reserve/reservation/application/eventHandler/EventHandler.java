package ticket.reserve.reservation.application.eventHandler;

import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventPayload;

public interface EventHandler <T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}

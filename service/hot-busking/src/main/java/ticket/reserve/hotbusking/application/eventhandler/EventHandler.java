package ticket.reserve.hotbusking.application.eventhandler;

import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;

public interface EventHandler <T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}

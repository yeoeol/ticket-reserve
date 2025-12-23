package ticket.reserve.inventory.application.eventhandler;

import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventPayload;

public interface EventHandler <T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}

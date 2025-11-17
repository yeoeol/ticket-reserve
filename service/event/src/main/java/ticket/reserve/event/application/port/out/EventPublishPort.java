package ticket.reserve.event.application.port.out;

import ticket.reserve.common.event.payload.EventCreatedEventPayload;

public interface EventPublishPort {
    void createEvent(EventCreatedEventPayload payload);
}

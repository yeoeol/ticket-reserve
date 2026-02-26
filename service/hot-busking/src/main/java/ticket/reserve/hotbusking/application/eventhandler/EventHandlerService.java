package ticket.reserve.hotbusking.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.inbox.Inbox;
import ticket.reserve.core.inbox.InboxRepository;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.hotbusking.application.HotBuskingScoreUpdater;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHandlerService {

    private final IdGenerator idGenerator;
    private final List<EventHandler> eventHandlers;
    private final InboxRepository inboxRepository;
    private final HotBuskingScoreUpdater hotBuskingScoreUpdater;

    public void handleEvent(Event<EventPayload> event) {
        if (inboxRepository.existsByEventId(event.getEventId())) {
            return;
        }

        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        try {
            if (isBuskingDeleted(event)) {
                eventHandler.handle(event);
            } else {
                hotBuskingScoreUpdater.update(event, eventHandler);
            }
            inboxRepository.saveAndFlush(Inbox.create(idGenerator, event.getEventId(), event.getType()));
        } catch (DataIntegrityViolationException e) {
            log.warn("[HotBuskingService.handleEvent] 중복 이벤트 감지: eventId={}, eventType={}", event.getEventId(), event.getType());
        }
    }

    private boolean isBuskingDeleted(Event<EventPayload> event) {
        return EventType.BUSKING_DELETED == event.getType();
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }
}

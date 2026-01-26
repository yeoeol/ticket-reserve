package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.inbox.Inbox;
import ticket.reserve.core.inbox.InboxRepository;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.eventhandler.EventHandler;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHandlerService {

    private final IdGenerator idGenerator;
    private final List<EventHandler> eventHandlers;
    private final InboxRepository inboxRepository;

    @Transactional
    public void handleEvent(Event<EventPayload> event) {
        if (inboxRepository.existsByEventId(event.getEventId())) {
            return;
        }

        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        try {
            eventHandler.handle(event);
            inboxRepository.saveAndFlush(Inbox.create(idGenerator, event.getEventId(), event.getType()));
        } catch (DataIntegrityViolationException e) {
            log.warn("[EventHandlerService.handleEvent] 중복 이벤트 감지: eventId={}, eventType={}", event.getEventId(), event.getType());
        }
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }
}

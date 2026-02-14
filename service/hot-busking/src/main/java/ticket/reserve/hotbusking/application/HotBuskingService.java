package ticket.reserve.hotbusking.application;

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
import ticket.reserve.hotbusking.application.dto.response.HotBuskingResponseDto;
import ticket.reserve.hotbusking.application.eventhandler.EventHandler;
import ticket.reserve.hotbusking.application.port.out.BuskingPort;
import ticket.reserve.hotbusking.infrastructure.persistence.redis.HotBuskingListRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotBuskingService {

    private final IdGenerator idGenerator;
    private final List<EventHandler> eventHandlers;
    private final InboxRepository inboxRepository;
    private final HotBuskingScoreUpdater hotBuskingScoreUpdater;
    private final HotBuskingListRepository hotBuskingListRepository;
    private final BuskingPort buskingPort;

    public void handleEvent(Event<EventPayload> event) {
        if (inboxRepository.existsByEventId(event.getEventId())) {
            return;
        }

        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        try {
            if (isSubscriptionCreatedOrDeleted(event)) {
                eventHandler.handle(event);
                inboxRepository.saveAndFlush(Inbox.create(idGenerator, event.getEventId(), event.getType()));
            } else {
                hotBuskingScoreUpdater.update(event, eventHandler);
            }
        } catch (DataIntegrityViolationException e) {
            log.warn("[HotBuskingService.handleEvent] 중복 이벤트 감지: eventId={}, eventType={}", event.getEventId(), event.getType());
        }
    }

    private boolean isSubscriptionCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.SUBSCRIPTION_CREATED == event.getType();
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }

    public List<HotBuskingResponseDto> readAll() {
        return hotBuskingListRepository.readAll().stream()
                .map(buskingPort::get)
                .filter(Objects::nonNull)
                .map(HotBuskingResponseDto::from)
                .toList();
    }
}

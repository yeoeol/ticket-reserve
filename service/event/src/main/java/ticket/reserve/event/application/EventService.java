package ticket.reserve.event.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.event.application.port.out.InventoryPort;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.dto.request.EventRequestDto;
import ticket.reserve.event.application.dto.response.EventResponseDto;
import ticket.reserve.event.application.dto.request.EventUpdateRequestDto;
import ticket.reserve.event.domain.Event;
import ticket.reserve.event.domain.repository.EventRepository;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.tsid.IdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final InventoryPort inventoryPort;
    private final OutboxEventPublisher outboxEventPublisher;
    private final IdGenerator idGenerator;

    @Transactional
    public EventDetailResponseDto createEvent(EventRequestDto request) {
        Event event = request.toEntity(idGenerator);
        Event savedEvent = eventRepository.save(event);

        outboxEventPublisher.publish(
                EventType.EVENT_CREATED,
                EventCreatedEventPayload.builder()
                        .eventId(event.getId())
                        .eventTitle(event.getEventTitle())
                        .description(event.getDescription())
                        .location(event.getLocation())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .totalInventoryCount(event.getTotalInventoryCount())
                        .build(),
                savedEvent.getId()
        );

        return EventDetailResponseDto.from(savedEvent, savedEvent.getTotalInventoryCount());
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDetailResponseDto getEvent(Long eventId) {
        Integer availableInventoryCount = inventoryPort.countsInventory(eventId);

        return eventRepository.findById(eventId)
                .map(e -> EventDetailResponseDto.from(e, availableInventoryCount))
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
    }

    @Transactional
    public void updateEvent(Long id, EventUpdateRequestDto request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        event.update(request);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Long> getEventIds() {
        return eventRepository.findEventIds();
    }
}

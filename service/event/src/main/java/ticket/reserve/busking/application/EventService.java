package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.application.dto.response.EventDetailResponseDto;
import ticket.reserve.busking.application.dto.request.EventRequestDto;
import ticket.reserve.busking.application.dto.request.EventUpdateRequestDto;
import ticket.reserve.busking.domain.event.Event;
import ticket.reserve.busking.domain.event.repository.EventRepository;
import ticket.reserve.busking.domain.eventimage.enums.ImageType;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.tsid.IdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final InventoryPort inventoryPort;
    private final ImagePort imagePort;
    private final OutboxEventPublisher outboxEventPublisher;
    private final IdGenerator idGenerator;

    @Transactional
    public EventDetailResponseDto createEvent(EventRequestDto request, MultipartFile file) {
        Event event = request.toEntity(idGenerator);
        if (file != null && !file.isEmpty()) {
            ImageResponseDto imageResponse = imagePort.uploadImage(file);
            event.addEventImage(
                    idGenerator, imageResponse.getOriginalFileName(), imageResponse.getStoredPath(),
                    ImageType.THUMBNAIL, 1
            );
        }

        Event savedEvent = eventRepository.save(event);
        outboxEventPublisher.publish(
                EventType.EVENT_CREATED,
                BuskingCreatedEventPayload.builder()
                        .buskingId(event.getId())
                        .title(event.getEventTitle())
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
    public List<EventDetailResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(e -> EventDetailResponseDto.from(e, inventoryPort.countsInventory(e.getId())))
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

        event.update(
                request.eventTitle(), request.description(), request.location(),
                request.startTime(), request.endTime(), request.totalInventoryCount()
        );
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

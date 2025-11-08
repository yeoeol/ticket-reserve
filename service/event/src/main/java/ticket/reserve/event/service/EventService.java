package ticket.reserve.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.event.client.InventoryServiceClient;
import ticket.reserve.event.dto.EventDetailResponseDto;
import ticket.reserve.event.dto.EventRequestDto;
import ticket.reserve.event.dto.EventResponseDto;
import ticket.reserve.event.dto.EventUpdateRequestDto;
import ticket.reserve.event.domain.Event;
import ticket.reserve.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final InventoryServiceClient inventoryServiceClient;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public EventResponseDto createEvent(EventRequestDto request) {
        Event event = request.toEntity();
        return EventResponseDto.from(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDetailResponseDto getEvent(Long eventId) {
        Integer availableInventoryCount = inventoryServiceClient.countsInventory(eventId);

        return eventRepository.findById(eventId)
                .map(e -> EventDetailResponseDto.from(e, availableInventoryCount))
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateRequestDto request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.update(request);

        return EventResponseDto.from(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}

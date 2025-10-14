package ticket.reserve.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public EventResponseDto getEvent(Long id) {
        return eventRepository.findById(id)
                .map(EventResponseDto::from)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateRequestDto request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.update(request);

        return EventResponseDto.from(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}

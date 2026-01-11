package ticket.reserve.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.port.out.EventPort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final EventPort eventPort;

    public List<EventDetailResponseDto> getEvents() {
        return eventPort.getEvents();
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        return eventPort.getEvent(eventId);
    }

    public EventDetailResponseDto createEvent(EventRequestDto request) {
        return eventPort.createEvent(request);
    }

    public void updateEvent(Long eventId, EventUpdateRequestDto request) {
        eventPort.updateEvent(eventId, request);
    }

    public void deleteEvent(Long eventId) {
        eventPort.deleteEvent(eventId);
    }
}

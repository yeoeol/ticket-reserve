package ticket.reserve.admin.application.port.out;

import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.dto.event.response.EventResponseDto;

import java.util.List;

public interface EventPort {
    List<EventResponseDto> getEvents();

    EventDetailResponseDto getEvent(Long eventId);

    EventDetailResponseDto createEvent(EventRequestDto request);

    void updateEvent(Long eventId, EventUpdateRequestDto request);

    void deleteEvent(Long eventId);
}

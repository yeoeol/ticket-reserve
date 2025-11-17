package ticket.reserve.admin.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventResponseDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.port.out.EventPort;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient extends EventPort {

    @GetMapping("/api/events")
    List<EventResponseDto> getEvents();

    @GetMapping("/api/events/{id}")
    EventDetailResponseDto getEvent(@PathVariable("id") Long eventId);

    @PostMapping("/api/events")
    EventDetailResponseDto createEvent(@RequestBody EventRequestDto request);

    @PutMapping("/api/events/{id}")
    void updateEvent(@PathVariable("id") Long eventId,
                     @RequestBody EventUpdateRequestDto request);

    @DeleteMapping("/api/events/{id}")
    void deleteEvent(@PathVariable("id") Long id);
}

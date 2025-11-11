package ticket.reserve.admin.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;
import ticket.reserve.admin.client.event.dto.EventRequestDto;
import ticket.reserve.admin.client.event.dto.EventResponseDto;
import ticket.reserve.admin.client.event.dto.EventUpdateRequestDto;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {

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

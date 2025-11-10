package ticket.reserve.admin.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;
import ticket.reserve.admin.client.event.dto.EventRequestDto;
import ticket.reserve.admin.client.event.dto.EventResponseDto;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {

    @GetMapping("/api/events")
    List<EventResponseDto> getEvents();

    @GetMapping("/api/events/{id}")
    EventDetailResponseDto getEvent(@PathVariable("id") Long eventId);

    @PostMapping("/api/events")
    ResponseEntity<EventDetailResponseDto> createEvent(@RequestBody EventRequestDto request);

    @DeleteMapping("/api/events/{id}")
    void deleteEvent(@PathVariable("id") Long id);
}

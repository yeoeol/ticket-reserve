package ticket.reserve.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.event.dto.EventDetailResponseDto;
import ticket.reserve.event.dto.EventRequestDto;
import ticket.reserve.event.dto.EventResponseDto;
import ticket.reserve.event.dto.EventUpdateRequestDto;
import ticket.reserve.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    @GetMapping("/api/events")
    public ResponseEntity<List<EventResponseDto>> getEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/api/events/{id}")
    public ResponseEntity<EventDetailResponseDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping("/api/events")
    public ResponseEntity<EventDetailResponseDto> createEvent(@RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @PutMapping("/api/events/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable("id") Long eventId,
                                            @RequestBody EventUpdateRequestDto request) {
        eventService.updateEvent(eventId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}

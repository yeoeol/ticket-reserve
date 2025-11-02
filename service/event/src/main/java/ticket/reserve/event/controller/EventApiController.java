package ticket.reserve.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.event.dto.EventRequestDto;
import ticket.reserve.event.dto.EventResponseDto;
import ticket.reserve.event.dto.EventUpdateRequestDto;
import ticket.reserve.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventApiController {

    private final EventService eventService;

    @PostMapping("/admin/events")
    public ResponseEntity<EventResponseDto> create(@RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDto>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PutMapping("/admin/events/{id}")
    public ResponseEntity<EventResponseDto> update(@PathVariable Long id,
                                       @RequestBody EventUpdateRequestDto request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

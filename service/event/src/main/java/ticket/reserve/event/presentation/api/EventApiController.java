package ticket.reserve.event.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.dto.request.EventRequestDto;
import ticket.reserve.event.application.dto.response.EventResponseDto;
import ticket.reserve.event.application.dto.request.EventUpdateRequestDto;
import ticket.reserve.event.application.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDetailResponseDto>> getEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailResponseDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping
    public ResponseEntity<EventDetailResponseDto> createEvent(@Valid @RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.createEvent(request, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable("id") Long eventId,
                                            @RequestBody EventUpdateRequestDto request) {
        eventService.updateEvent(eventId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event-ids")
    public ResponseEntity<List<Long>> getEventIds() {
        return ResponseEntity.ok(eventService.getEventIds());
    }
}

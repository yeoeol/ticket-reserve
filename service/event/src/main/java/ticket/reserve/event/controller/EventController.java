package ticket.reserve.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.event.dto.EventRequestDto;
import ticket.reserve.event.dto.EventResponseDto;
import ticket.reserve.event.dto.EventUpdateRequestDto;
import ticket.reserve.event.service.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/admin/events")
    public ResponseEntity<EventResponseDto> create(@RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping("/events")
    public String getAll(Model model) {
        List<EventResponseDto> eventList = eventService.getAllEvents();
        model.addAttribute("eventList", eventList);

        return "event-list";
    }

    @GetMapping("/events/{id}")
    public String getOne(@PathVariable Long id, Model model) {
        EventResponseDto event = eventService.getEvent(id);
        model.addAttribute("event", event);

        return "event-detail";
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

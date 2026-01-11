package ticket.reserve.admin.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.AdminEventService;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @GetMapping
    public String getEvents(Model model) {
        List<EventDetailResponseDto> events = adminEventService.getEvents();

        model.addAttribute("events", events);
        return "admin/events";
    }

    @GetMapping("/{id}")
    public String getEvent(@PathVariable("id") Long eventId, Model model) {
        EventDetailResponseDto event = adminEventService.getEvent(eventId);

        model.addAttribute("event", event);
        return "admin/eventdetails";
    }

    @GetMapping("/new")
    public String createEventPage(Model model) {
        model.addAttribute("event", new EventRequestDto());
        return "admin/eventdetails";
    }

    @PostMapping
    public String createEvent(@ModelAttribute EventRequestDto request) {
        adminEventService.createEvent(request);
        return "redirect:/admin/events";
    }

    @PutMapping("/{id}")
    public String updateEvent(@PathVariable("id") Long eventId,
                              @ModelAttribute EventUpdateRequestDto request) {
        adminEventService.updateEvent(eventId, request);
        return "redirect:/admin/events";
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId) {
        adminEventService.deleteEvent(eventId);
        return "redirect:/admin/events";
    }
}

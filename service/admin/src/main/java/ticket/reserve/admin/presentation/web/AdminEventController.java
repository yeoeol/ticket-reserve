package ticket.reserve.admin.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.dto.event.response.EventResponseDto;
import ticket.reserve.admin.application.AdminService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminService adminService;

    @GetMapping
    public String getEvents(Model model) {
        List<EventResponseDto> events = adminService.getEvents();

        model.addAttribute("events", events);
        return "admin/events";
    }

    @GetMapping("/{id}")
    public String getEvent(@PathVariable("id") Long eventId, Model model) {
        EventDetailResponseDto event = adminService.getEvent(eventId);

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
        adminService.createEvent(request);
        return "redirect:/admin/events";
    }

    @PutMapping("/{id}")
    public String updateEvent(@PathVariable("id") Long eventId,
                              @ModelAttribute EventUpdateRequestDto request) {
        adminService.updateEvent(eventId, request);
        return "redirect:/admin/events";
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId) {
        adminService.deleteEvent(eventId);
        return "redirect:/admin/events";
    }
}

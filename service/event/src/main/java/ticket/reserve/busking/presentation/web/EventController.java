package ticket.reserve.busking.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.request.EventRequestDto;
import ticket.reserve.busking.application.dto.response.EventDetailResponseDto;
import ticket.reserve.busking.application.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public String getAll(Model model) {
        List<EventDetailResponseDto> eventList = eventService.getAllEvents();
        model.addAttribute("eventList", eventList);

        return "event-list";
    }

    @GetMapping("/{id}")
    public String getOne(
            @PathVariable("id") Long eventId,
            @AuthenticationPrincipal String userId,
            Model model
    ) {
        EventDetailResponseDto event = eventService.getEvent(eventId);
        model.addAttribute("event", event);
        model.addAttribute("isAuthenticated", userId != null);

        return "event-detail";
    }

    @GetMapping("/new")
    public String createEventPage() {
        return "event-create";
    }

    @PostMapping
    public String createEvent(
            EventRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        EventDetailResponseDto response = eventService.createEvent(request, file);
        return "redirect:/events/%d".formatted(response.id());
    }
}

package ticket.reserve.event.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.event.application.dto.request.EventRequestDto;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.dto.response.EventResponseDto;
import ticket.reserve.event.application.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public String getAll(Model model) {
        List<EventResponseDto> eventList = eventService.getAllEvents();
        model.addAttribute("eventList", eventList);

        return "event-list";
    }

    @GetMapping("/{id}")
    public String getOne(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false, defaultValue = "0") String userId,
            Model model
    ) {
        EventDetailResponseDto event = eventService.getEvent(id);
        model.addAttribute("event", event);
        model.addAttribute("isAuthenticated", !userId.equals("0"));

        return "event-detail";
    }

    @GetMapping("/new")
    public String createEventPage() {
        return "event-create";
    }

    @PostMapping
    public String createEvent(
            EventRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal String userId
    ) {
        EventDetailResponseDto response = eventService.createEvent(request, file, userId);
        return "redirect:/events/%d".formatted(response.id());
    }
}

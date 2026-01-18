package ticket.reserve.busking.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.BuskingService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class BuskingController {

    private final BuskingService buskingService;

    @GetMapping
    public String getAll(Model model) {
        List<BuskingResponseDto> eventList = buskingService.getAllEvents();
        model.addAttribute("eventList", eventList);

        return "event-list";
    }

    @GetMapping("/{id}")
    public String getOne(
            @PathVariable("id") Long eventId,
            @AuthenticationPrincipal String userId,
            Model model
    ) {
        BuskingResponseDto event = buskingService.getEvent(eventId);
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
            BuskingRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        BuskingResponseDto response = buskingService.createEvent(request, file);
        return "redirect:/events/%d".formatted(response.id());
    }
}

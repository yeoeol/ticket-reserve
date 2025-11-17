package ticket.reserve.event.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.dto.response.EventResponseDto;
import ticket.reserve.event.application.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public String getAll(Model model) {
        List<EventResponseDto> eventList = eventService.getAllEvents();
        model.addAttribute("eventList", eventList);

        return "event-list";
    }

    @GetMapping("/events/{id}")
    public String getOne(@PathVariable Long id, Model model) {
        EventDetailResponseDto event = eventService.getEvent(id);
        model.addAttribute("event", event);

        return "event-detail";
    }

}

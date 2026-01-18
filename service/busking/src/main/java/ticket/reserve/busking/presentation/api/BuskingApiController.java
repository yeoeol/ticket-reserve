package ticket.reserve.busking.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.BuskingService;

import java.util.List;

import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class BuskingApiController {

    private final BuskingService buskingService;

    @GetMapping
    public ResponseEntity<List<BuskingResponseDto>> getEvents() {
        return ResponseEntity.ok(buskingService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuskingResponseDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(buskingService.getEvent(id));
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BuskingResponseDto> createEvent(
            @Valid @RequestPart(value = "request") BuskingRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(buskingService.createEvent(request, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable("id") Long eventId,
                                            @RequestBody BuskingUpdateRequestDto request) {
        buskingService.updateEvent(eventId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long eventId) {
        buskingService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event-ids")
    public ResponseEntity<List<Long>> getEventIds() {
        return ResponseEntity.ok(buskingService.getEventIds());
    }
}

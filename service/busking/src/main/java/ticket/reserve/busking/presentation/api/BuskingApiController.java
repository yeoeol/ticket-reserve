package ticket.reserve.busking.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.SearchService;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.BuskingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buskings")
public class BuskingApiController {

    private final BuskingService buskingService;
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<BuskingResponseDto>> getAll(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime
    ) {
        return ResponseEntity.ok(searchService.search(title, location, startTime, endTime));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuskingResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(buskingService.getOne(id));
    }

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BuskingResponseDto> create(
            @Valid @RequestPart(value = "request") BuskingRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(buskingService.create(request, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long buskingId,
                                       @RequestBody BuskingUpdateRequestDto request) {
        buskingService.update(buskingId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long buskingId) {
        buskingService.delete(buskingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getIds() {
        return ResponseEntity.ok(buskingService.getIds());
    }
}

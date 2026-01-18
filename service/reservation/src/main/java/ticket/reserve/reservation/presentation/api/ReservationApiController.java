package ticket.reserve.reservation.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.reservation.application.dto.response.QueueStatusResponseDto;
import ticket.reserve.reservation.infrastructure.persistence.QueueService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final QueueService queueService;

    @PostMapping("/{id}/queue/register")
    public ResponseEntity<QueueStatusResponseDto> registerQueue(
            @PathVariable("id") Long buskingId,
            @RequestHeader(value = "X-USER-ID", required = false, defaultValue = "0") String userId
    ) {
        return ResponseEntity.ok(queueService.registerWaitingQueue(buskingId, userId));
    }

    @GetMapping("/{id}/queue/rank")
    public ResponseEntity<QueueStatusResponseDto> getRank(
            @PathVariable("id") Long buskingId,
            @RequestHeader(value = "X-USER-ID", required = false, defaultValue = "0") String userId
    ) {
        return ResponseEntity.ok(queueService.getQueueStatus(buskingId, userId));
    }
}

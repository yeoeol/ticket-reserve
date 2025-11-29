package ticket.reserve.reservation.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.reservation.application.ReservationService;
import ticket.reserve.reservation.application.dto.response.QueueStatusResponseDto;
import ticket.reserve.reservation.infrastucture.persistence.QueueService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationService reservationService;
    private final QueueService queueService;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable("id") Long reservationId) {
        reservationService.confirmReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseReservation(@PathVariable("id") Long reservationId) {
        reservationService.releaseReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/queue/register")
    public ResponseEntity<QueueStatusResponseDto> registerQueue(
            @PathVariable("eventId") Long eventId,
            @RequestHeader(value = "X-USER-ID", required = false, defaultValue = "0") String userId
    ) {
        return ResponseEntity.ok(queueService.registerWaitingQueue(eventId, userId));
    }

    @GetMapping("/{eventId}/queue/rank")
    public ResponseEntity<QueueStatusResponseDto> getRank(
            @PathVariable("eventId") Long eventId,
            @RequestHeader(value = "X-USER-ID", required = false, defaultValue = "0") String userId
    ) {
        return ResponseEntity.ok(queueService.getQueueStatus(eventId, Long.parseLong(userId)));
    }
}

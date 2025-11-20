package ticket.reserve.reservation.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.reservation.application.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationService reservationService;

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
}

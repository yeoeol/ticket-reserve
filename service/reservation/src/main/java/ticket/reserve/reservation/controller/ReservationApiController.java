package ticket.reserve.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @PostMapping("/api/reservations/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable("id") Long reservationId) {
        reservationService.confirmReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/reservations/{id}/release")
    public ResponseEntity<Void> releaseReservation(@PathVariable("id") Long reservationId) {
        reservationService.releaseReservation(reservationId);
        return ResponseEntity.ok().build();
    }
}

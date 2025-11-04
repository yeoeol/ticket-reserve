package ticket.reserve.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.reservation.dto.ReservationRequestDto;
import ticket.reserve.reservation.dto.ReservationResponseDto;
import ticket.reserve.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDto> create(
            @RequestBody ReservationRequestDto request,
            @AuthenticationPrincipal Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        ReservationResponseDto response = reservationService.createReservation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

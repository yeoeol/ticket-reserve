package ticket.reserve.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.reservation.dto.ReservationRequestDto;
import ticket.reserve.reservation.dto.ReservationResponseDto;
import ticket.reserve.reservation.service.ReservationService;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public String create(
            @ModelAttribute ReservationRequestDto request,
            @AuthenticationPrincipal String userId
    ) {
        ReservationResponseDto response = reservationService.createReservation(request, Long.parseLong(userId));

        return "redirect:/payments?userId=%s&reservationId=%d&inventoryId=%d&amount=%d".formatted(
                userId, response.reservationId(), response.inventoryId(), response.price()
        );
    }
}

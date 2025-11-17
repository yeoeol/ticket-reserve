package ticket.reserve.reservation.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.reservation.application.dto.request.ReservationRequestDto;
import ticket.reserve.reservation.application.dto.response.ReservationResponseDto;
import ticket.reserve.reservation.application.ReservationService;

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

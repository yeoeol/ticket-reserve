package ticket.reserve.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "RESERVATION-SERVICE")
public interface ReservationServiceClient {

    /**
     * reservation-service의 /api/reservation/{id}/confirm API를 호출
     */
    @PostMapping("/api/reservations/{id}/confirm")
    void confirmReservation(@PathVariable("id") Long reservationId);

    /**
     * reservation-service의 /api/reservation/{id}/release API를 호출
     */
    @PostMapping("/api/reservations/{id}/release")
    void releaseReservation(@PathVariable("id") Long reservationId);
}

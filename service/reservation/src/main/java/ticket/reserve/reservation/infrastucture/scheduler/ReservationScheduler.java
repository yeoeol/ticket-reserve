package ticket.reserve.reservation.infrastucture.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.reservation.application.ReservationExpiryService;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationExpiryService reservationExpiryService;

    // 1분마다 실행
    @Scheduled(cron = "0 0/1 * * * ?")
    public void triggerExpiredReservation() {
        reservationExpiryService.findAndPublishExpiredReservations();
    }
}

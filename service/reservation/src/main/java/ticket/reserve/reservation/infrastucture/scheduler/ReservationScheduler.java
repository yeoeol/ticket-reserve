package ticket.reserve.reservation.infrastucture.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.reservation.application.ReservationExpiryService;
import ticket.reserve.reservation.infrastucture.persistence.QueueService;

import static java.util.concurrent.TimeUnit.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationExpiryService reservationExpiryService;
    private final QueueService queueService;

    // 1분마다 실행
    @Scheduled(fixedDelay = 1L, timeUnit = MINUTES)
    @SchedulerLock(
            name = "reserveExpiryLock",
            lockAtMostFor = "50s",
            lockAtLeastFor = "30s"
    )
    public void triggerExpiredReservation() {
        reservationExpiryService.findAndPublishExpiredReservations();
    }

    // 3초마다 실행
    @Scheduled(fixedDelay = 3L, timeUnit = SECONDS)
    @SchedulerLock(
            name = "queueProcessLock",
            lockAtMostFor = "2s",
            lockAtLeastFor = "1s"
    )
    public void processQueue() {
        queueService.processQueue();
    }
}

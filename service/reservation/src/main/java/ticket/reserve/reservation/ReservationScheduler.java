package ticket.reserve.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.producer.ReservationExpiredProducer;
import ticket.reserve.reservation.repository.ReservationRepository;
import ticket.reserve.reservation.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationExpiredProducer reservationExpiredProducer;

    private static final int RESERVATION_TIMEOUT_MINUTES = 5;

    // 1분마다 실행
    @Scheduled(cron = "0 0/1 * * * ?")
    public void releaseExpiredReservations() {
        log.info("[ReservationScheduler.releaseExpiredReservations] 만료된 PENDING 예매 정리 스케줄러 실행");

        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(RESERVATION_TIMEOUT_MINUTES);

        // DB에서 5분이 지난 PENDING 상태의 예매 목록 가져오기
        List<Reservation> expiredReservations = reservationRepository
                .findAllByStatusAndCreatedAtBefore(ReservationStatus.PENDING, expirationTime);

        if (expiredReservations.isEmpty()) {
            log.info("[ReservationScheduler.releaseExpiredReservations] 만료된 예매 없음");
            return;
        }

        log.warn("[ReservationScheduler.releaseExpiredReservations] {}건의 만료된 예매 롤백", expiredReservations.size());
        // 롤백 로직 실행
        for (Reservation reservation : expiredReservations) {
            ReservationExpiredPayload payload = ReservationExpiredPayload.builder()
                    .reservationId(reservation.getId())
                    .inventoryId(reservation.getInventoryId())
                    .eventId(reservation.getEventId())
                    .userId(reservation.getUserId())
                    .build();
            reservationExpiredProducer.expireReservation(payload);

            log.error("예매 롤백 이벤트 발행 (reservationId : {})", reservation.getId());
        }
    }
}

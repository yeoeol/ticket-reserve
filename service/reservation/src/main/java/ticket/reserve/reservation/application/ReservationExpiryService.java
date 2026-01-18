package ticket.reserve.reservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.ReservationExpiredPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationExpiryService {

    private final ReservationRepository reservationRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    private static final int RESERVATION_TIMEOUT_MINUTES = 5;

    @Transactional
    public void findAndPublishExpiredReservations() {
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
            outboxEventPublisher.publish(
                    EventType.RESERVATION_EXPIRED,
                    ReservationExpiredPayload.builder()
                            .reservationId(reservation.getId())
                            .inventoryId(reservation.getInventoryId())
                            .buskingId(reservation.getBuskingId())
                            .userId(reservation.getUserId())
                            .build(),
                    reservation.getBuskingId()
            );
            log.error("예매 롤백 이벤트 발행 (reservationId : {})", reservation.getId());
        }
    }
}

package ticket.reserve.reservation.application.eventHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredEventHandler implements EventHandler<ReservationExpiredPayload> {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(Event<ReservationExpiredPayload> event) {
        ReservationExpiredPayload payload = event.getPayload();
        releaseReservation(payload.getReservationId());
        log.info("[ReservationExpiredEventHandler.handle] 예매 취소 처리 완료 - reservationId = {}", payload.getReservationId());
    }

    @Override
    public boolean supports(Event<ReservationExpiredPayload> event) {
        return EventType.RESERVATION_EXPIRED == event.getType();
    }

    private void releaseReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.release();
    }
}

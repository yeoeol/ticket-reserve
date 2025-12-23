package ticket.reserve.reservation.application.eventHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedEventHandler implements EventHandler<PaymentConfirmedEventPayload> {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(Event<PaymentConfirmedEventPayload> event) {
        PaymentConfirmedEventPayload payload = event.getPayload();
        confirmReservation(payload.getReservationId());
        log.info("[PaymentConfirmedEventHandler.handle] 예매 확정 처리 완료 - reservationId = {}", payload.getReservationId());
    }

    @Override
    public boolean supports(Event<PaymentConfirmedEventPayload> event) {
        return EventType.PAYMENT_CONFIRMED == event.getType();
    }

    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (isValidRequest(reservation)) return;

        reservation.confirm();
    }

    private static boolean isValidRequest(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            log.error("결제는 성공했으나 예매 시간 초과로 취소되었습니다. 환불 로직이 필요합니다. reservationId={}", reservation.getId());
            return true;
        }
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return true;
        }
        return false;
    }
}

package ticket.reserve.reservation.infrastucture.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.ReservationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedEventConsumer {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = EventType.Topic.TICKET_RESERVE_PAYMENT)
    public void listen(String message) {
        log.info("[PaymentConfirmedEventConsumer.listen] 결제 완료 이벤트 수신: message = {}", message);
        try {
            PaymentConfirmedEventPayload payload = objectMapper.readValue(message, PaymentConfirmedEventPayload.class);
            reservationService.confirmReservation(payload.getReservationId());
            log.info("[PaymentConfirmedEventConsumer.listen] 예매 확정 처리 완료 - reservationId = {}", payload.getReservationId());
        } catch (Exception e) {
            log.error("[PaymentConfirmedEventConsumer.listen]", e);
            throw new CustomException(ErrorCode.PAYMENT_CONFIRMED_ERROR);
        }
    }
}

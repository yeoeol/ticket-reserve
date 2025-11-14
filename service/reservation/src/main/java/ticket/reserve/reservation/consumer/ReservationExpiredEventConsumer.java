package ticket.reserve.reservation.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.reservation.service.ReservationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredEventConsumer {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = EventType.Topic.TICKET_RESERVE_RESERVATION)
    public void listen(String message) {
        log.info("[ReservationExpiredEventConsumer.listen] 예매 만료 이벤트 수신: message = {}", message);
        try {
            PaymentConfirmedEventPayload payload = objectMapper.readValue(message, PaymentConfirmedEventPayload.class);
            reservationService.releaseReservation(payload.getReservationId());
            log.info("[ReservationExpiredEventConsumer.listen] 예매 취소 처리 완료 - reservationId = {}", payload.getReservationId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[ReservationExpiredEventConsumer.listen] JSON 파싱 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("[ReservationExpiredEventConsumer.listen] 오류 발생", e);
        }
    }
}

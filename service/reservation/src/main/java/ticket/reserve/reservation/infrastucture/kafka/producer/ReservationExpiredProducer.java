package ticket.reserve.reservation.infrastucture.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.port.out.ReservationPublishPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredProducer implements ReservationPublishPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void expireReservation(ReservationExpiredPayload payload) {
        log.info("[ReservationExpiredEventConsumer.listen] 예매 만료 이벤트 발행: payload = {}", payload);
        String reservationExpiredMessage = null;
        try {
            reservationExpiredMessage = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("[ReservationExpiredProducer.expireReservation]", e);
            throw new CustomException(ErrorCode.RESERVATION_EXPIRED_ERROR);
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_RESERVATION, reservationExpiredMessage);
        log.info("[ReservationExpiredProducer.expireReservation] " +
                "Kafka 메시지 전송 완료 - reservationId={}, message={}", payload.getReservationId(), reservationExpiredMessage);
    }
}

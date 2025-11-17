package ticket.reserve.reservation.infrastucture.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.reservation.application.port.out.ReservationPublishPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredProducer implements ReservationPublishPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void expireReservation(ReservationExpiredPayload payload) {
        String reservationExpiredMessage = null;
        try {
            reservationExpiredMessage = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[ReservationExpiredProducer.expireReservation] JSON 파싱 과정 중 오류 발생");
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_RESERVATION, reservationExpiredMessage);
        log.info("[ReservationExpiredProducer.expireReservation] " +
                "Kafka 메시지 전송 완료 - reservationId={}, message={}", payload.getReservationId(), reservationExpiredMessage);
    }
}

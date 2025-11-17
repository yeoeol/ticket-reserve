package ticket.reserve.payment.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.payment.application.port.out.PaymentPublishPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedProducer implements PaymentPublishPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void paymentConfirmEvent(PaymentConfirmedEventPayload payload) {
        String paymentConfirmedMessage = null;
        try {
            paymentConfirmedMessage = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[PaymentConfirmedProducer.paymentConfirmEvent] JSON 파싱 과정 중 오류 발생");
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_PAYMENT, paymentConfirmedMessage);
        log.info("[PaymentConfirmedProducer.paymentConfirmEvent] " +
                "Kafka 메시지 전송 완료 - reservationId={}, message={}", payload.getReservationId(), paymentConfirmedMessage);
    }
}

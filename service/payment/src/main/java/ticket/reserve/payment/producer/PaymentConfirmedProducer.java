package ticket.reserve.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.payment.domain.Payment;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void paymentConfirmEvent(Payment payment) {
        PaymentConfirmedEventPayload message = PaymentConfirmedEventPayload.builder()
                .reservationId(payment.getReservationId())
                .inventoryId(payment.getInventoryId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .build();

        String paymentConfirmedMessage = null;
        try {
            paymentConfirmedMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[PaymentConfirmedProducer.paymentConfirmEvent] JSON 파싱 과정 중 오류 발생");
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_PAYMENT, paymentConfirmedMessage);
        log.info("[PaymentConfirmedProducer.paymentConfirmEvent] " +
                "Kafka 메시지 전송 완료 - reservationId={}, message={}", payment.getReservationId(), paymentConfirmedMessage);
    }
}

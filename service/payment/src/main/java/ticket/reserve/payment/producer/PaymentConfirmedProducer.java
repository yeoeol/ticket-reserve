package ticket.reserve.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.payment.client.dto.PaymentConfirmedEvent;
import ticket.reserve.payment.domain.Payment;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedProducer {

    private static final String PAYMENT_CONFIRMED_TOPIC = "ticket-reserve-payment";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void paymentConfirmEvent(Payment payment) {
        PaymentConfirmedEvent message = PaymentConfirmedEvent.from(payment);

        String paymentConfirmedMessage = null;
        try {
            paymentConfirmedMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[PaymentConfirmedProducer.paymentConfirmEvent] JSON 파싱 과정 중 오류 발생");
        }

        kafkaTemplate.send(PAYMENT_CONFIRMED_TOPIC, paymentConfirmedMessage);
        log.info("[PaymentConfirmedProducer.paymentConfirmEvent] " +
                "Kafka 메시지 전송 완료 - reservationId={}, message={}", payment.getReservationId(), paymentConfirmedMessage);
    }
}

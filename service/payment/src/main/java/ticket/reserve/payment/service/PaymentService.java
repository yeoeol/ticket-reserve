package ticket.reserve.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.payment.client.TossPaymentsClient;
import ticket.reserve.payment.client.dto.TossResponseDto;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.dto.PaymentConfirmRequestDto;
import ticket.reserve.payment.producer.PaymentConfirmedProducer;
import ticket.reserve.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final PaymentConfirmedProducer paymentConfirmedProducer;

    @Transactional
    public void createPayment(String orderId, Long userId, Long reservationId, Long inventoryId) {
        Payment payment = Payment.builder()
                .userId(userId)
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .orderId(orderId)
                .build();

        paymentRepository.save(payment);
    }

    @Transactional
    public void confirmPayment(PaymentConfirmRequestDto request) {
        TossResponseDto tossResponseDto = tossPaymentsClient.confirmPayment(request);

        Payment payment = paymentRepository.findByOrderId(tossResponseDto.orderId())
                .orElseThrow(() -> new RuntimeException("Payment NOT FOUND : By orderId"));
        payment.setting(tossResponseDto);

        // OpenFeign - Reservation-service의 confirm 호출
//        reservationServiceClient.confirmReservation(payment.getReservationId());

        // Kafka - 비동기 결제 완료 이벤트 발행
        PaymentConfirmedEventPayload payload = PaymentConfirmedEventPayload.builder()
                .reservationId(payment.getReservationId())
                .inventoryId(payment.getInventoryId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .build();
        paymentConfirmedProducer.paymentConfirmEvent(payload);
    }

/*    @Component
    class PaymentTestListener {

        private static final String TEST_GROUP_ID = "payment-test-group";

        @KafkaListener(topics = "ticket-reserve-payment", groupId = TEST_GROUP_ID)
        public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
            System.out.println("========== KAFKA TEST LISTENER ==========");
            System.out.println("이벤트 수신 성공: " + event.getReservationId() + " | " + event.getInventoryId());
            System.out.println("=======================================");
        }
    }*/
}

package ticket.reserve.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.payment.application.port.out.PaymentPublishPort;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsPort tossPaymentsPort;
    private final PaymentPublishPort paymentPublishPort;

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
        TossResponseDto tossResponseDto = tossPaymentsPort.confirmPayment(request);

        Payment payment = paymentRepository.findByOrderId(tossResponseDto.orderId())
                .orElseThrow(() -> new RuntimeException("Payment NOT FOUND : By orderId"));

        payment.confirmPayment(
                tossResponseDto.paymentKey(),
                tossResponseDto.orderName(),
                tossResponseDto.status(),
                tossResponseDto.requestedAt().toLocalDateTime(),
                tossResponseDto.approvedAt().toLocalDateTime(),
                tossResponseDto.totalAmount()
        );

        // Kafka - 비동기 결제 완료 이벤트 발행
        PaymentConfirmedEventPayload payload = PaymentConfirmedEventPayload.builder()
                .reservationId(payment.getReservationId())
                .inventoryId(payment.getInventoryId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .build();
        paymentPublishPort.paymentConfirmEvent(payload);
    }
}

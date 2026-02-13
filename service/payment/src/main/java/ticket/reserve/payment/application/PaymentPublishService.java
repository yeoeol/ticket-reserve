package ticket.reserve.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.domain.Payment;

@Service
@RequiredArgsConstructor
public class PaymentPublishService {

    private final OutboxEventPublisher outboxEventPublisher;
    private final PaymentQueryService paymentQueryService;

    @Transactional
    public void updateInfoAndpublishPaymentConfirmedEvent(TossResponseDto response) {
        Payment payment = updatePaymentInfo(response);

        outboxEventPublisher.publish(
                EventType.PAYMENT_CONFIRMED,
                PaymentConfirmedEventPayload.builder()
                        .reservationId(payment.getReservationId())
                        .inventoryId(payment.getInventoryId())
                        .userId(payment.getUserId())
                        .orderId(payment.getOrderId())
                        .totalAmount(payment.getTotalAmount())
                        .build(),
                payment.getId()
        );
    }

    private Payment updatePaymentInfo(TossResponseDto response) {
        Payment payment = paymentQueryService.findByOrderId(response.orderId());
        payment.confirmPayment(
                response.paymentKey(),
                response.orderName(),
                response.status(),
                response.requestedAt().toLocalDateTime(),
                response.approvedAt().toLocalDateTime(),
                response.totalAmount()
        );
        return payment;
    }
}

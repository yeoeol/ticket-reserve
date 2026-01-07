package ticket.reserve.payment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    PaymentService paymentService;

    @Mock PaymentRepository paymentRepository;
    @Mock TossPaymentsPort tossPaymentsPort;
    @Mock OutboxEventPublisher outboxEventPublisher;

    @Test
    @DisplayName("결제 생성 성공 - 파라미터 정보를 바탕으로 결제 엔티티가 생성된다")
    void createPaymentSuccess() {
        //given
        Payment payment = createPayment(1L, 1L, 10L);

        //when
        paymentService.createPayment(payment.getOrderId(), 1L, 1L, 10L);

        //then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


    private Payment createPayment(Long userId, Long reservationId, Long inventoryId) {
        String orderId = UUID.randomUUID().toString().substring(0, 12);
        return Payment.builder()
                .userId(userId)
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .orderId(orderId)
                .build();
    }
}
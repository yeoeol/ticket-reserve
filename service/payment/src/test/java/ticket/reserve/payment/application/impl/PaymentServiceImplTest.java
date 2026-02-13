package ticket.reserve.payment.application.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.payment.application.PaymentPublishService;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TossPaymentsPort tossPaymentsPort;
    @Mock
    private PaymentPublishService paymentPublishService;

    @Test
    @DisplayName("결제 생성 성공 - 파라미터 정보를 바탕으로 결제 엔티티가 생성된다")
    void create_payment_success() {
        //given
        Payment payment = createPayment(1L, 1L, 1L, 10L);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        given(paymentRepository.save(captor.capture())).willReturn(payment);

        //when
        paymentService.createPayment(payment.getOrderId(), 1L, 1L, 10L);

        //then
        Payment savedPayment = captor.getValue();

        assertThat(savedPayment.getUserId()).isEqualTo(payment.getUserId());
        assertThat(savedPayment.getReservationId()).isEqualTo(payment.getReservationId());
        assertThat(savedPayment.getInventoryId()).isEqualTo(payment.getInventoryId());
        assertThat(savedPayment.getOrderId()).isEqualTo(payment.getOrderId());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    private Payment createPayment(Long paymentId, Long userId, Long reservationId, Long inventoryId) {
        String orderId = "test-order-" + paymentId;
        return Payment.create(
                () -> paymentId,
                userId,
                reservationId,
                inventoryId,
                orderId
        );
    }
}
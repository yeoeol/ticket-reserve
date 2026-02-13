package ticket.reserve.payment.application.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.payment.application.PaymentService;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

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
    private PaymentRepository paymentRepository;
    @Mock
    private TossPaymentsPort tossPaymentsPort;
    @Mock
    private OutboxEventPublisher outboxEventPublisher;
    @Mock
    private IdGenerator idGenerator;

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

    @Test
    @DisplayName("결제 승인 성공 - 토스 페이먼츠 API를 호출 후 받은 응답 값으로, 이전에 생성했던 결제 엔티티의 필드를 채운다")
    void confirmPaymentSuccess() {
        //given
        Payment payment = createPayment(1L, 1L, 1L, 10L);

        TossResponseDto tossResponseDto = new TossResponseDto(
                "testPaymentKey", "DONE", payment.getOrderId(), "testOrderName",
                OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(1), 5000, "creditCard"
        );
        PaymentConfirmRequestDto request = new PaymentConfirmRequestDto(
                payment.getOrderId(), tossResponseDto.paymentKey(), payment.getTotalAmount()
        );

        given(tossPaymentsPort.confirmPayment(any())).willReturn(tossResponseDto);
        given(paymentRepository.findByOrderId(payment.getOrderId())).willReturn(Optional.of(payment));

        //when
        paymentService.confirmPayment(request);

        //then
        assertThat(payment.getPaymentKey()).isEqualTo(tossResponseDto.paymentKey());
        assertThat(payment.getOrderName()).isEqualTo(tossResponseDto.orderName());
        assertThat(payment.getStatus()).isEqualTo(tossResponseDto.status());
        assertThat(payment.getRequestedAt()).isEqualTo(tossResponseDto.requestedAt().toLocalDateTime());
        assertThat(payment.getApprovedAt()).isEqualTo(tossResponseDto.approvedAt().toLocalDateTime());
        assertThat(payment.getTotalAmount()).isEqualTo(tossResponseDto.totalAmount());

        verify(outboxEventPublisher, times(1))
                .publish(eq(EventType.PAYMENT_CONFIRMED), any(PaymentConfirmedEventPayload.class), anyLong());
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
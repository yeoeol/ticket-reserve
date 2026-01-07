package ticket.reserve.payment.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
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
        Payment payment = createPayment(1L, 1L, 1L, 10L);

        //when
        paymentService.createPayment(payment.getOrderId(), 1L, 1L, 10L);

        //then
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
        String orderId = UUID.randomUUID().toString().substring(0, 12);
        return Payment.builder()
                .id(paymentId)
                .userId(userId)
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .orderId(orderId)
                .build();
    }
}
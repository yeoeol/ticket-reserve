package ticket.reserve.payment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.domain.Payment;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentPublishServiceTest {

    @InjectMocks
    private PaymentPublishService paymentPublishService;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;
    @Mock
    private PaymentQueryService paymentQueryService;

    @Test
    @DisplayName("결제 승인 성공 - 결제 API 호출 후 받은 응답 값으로, 이전에 생성했던 결제 엔티티의 필드를 채우고 이벤트를 발행한다")
    void confirmPaymentSuccess() {
        //given
        Payment payment = createPayment(1L, 1L, 1L, 10L);
        TossResponseDto tossResponseDto = new TossResponseDto(
                "testPaymentKey", "DONE", payment.getOrderId(), "testOrderName",
                OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(1), 5000, "creditCard"
        );

        given(paymentQueryService.findByOrderId(anyString())).willReturn(payment);

        //when
        paymentPublishService.updateInfoAndPublishPaymentConfirmedEvent(tossResponseDto);

        //then
        assertThat(payment.getPaymentKey()).isEqualTo(tossResponseDto.paymentKey());
        assertThat(payment.getOrderName()).isEqualTo(tossResponseDto.orderName());
        assertThat(payment.getStatus()).isEqualTo(tossResponseDto.status());
        assertThat(payment.getRequestedAt()).isEqualTo(tossResponseDto.requestedAt().toLocalDateTime());
        assertThat(payment.getApprovedAt()).isEqualTo(tossResponseDto.approvedAt().toLocalDateTime());
        assertThat(payment.getTotalAmount()).isEqualTo(tossResponseDto.totalAmount());

        verify(outboxEventPublisher, times(1)).publish(
                eq(EventType.PAYMENT_CONFIRMED),
                any(PaymentConfirmedEventPayload.class),
                anyLong()
        );
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
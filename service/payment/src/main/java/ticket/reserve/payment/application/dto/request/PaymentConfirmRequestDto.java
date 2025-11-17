package ticket.reserve.payment.application.dto.request;

public record PaymentConfirmRequestDto(
        String orderId,
        String paymentKey,
        int amount
) {
}

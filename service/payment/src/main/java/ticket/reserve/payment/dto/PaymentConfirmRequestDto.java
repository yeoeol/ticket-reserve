package ticket.reserve.payment.dto;

public record PaymentConfirmRequestDto(
        String orderId,
        String paymentKey,
        int amount
) {
}

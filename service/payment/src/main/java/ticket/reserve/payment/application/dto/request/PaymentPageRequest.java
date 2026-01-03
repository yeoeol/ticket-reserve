package ticket.reserve.payment.application.dto.request;

public record PaymentPageRequest(
        Long userId,
        Long reservationId,
        Long inventoryId,
        int amount
) {
}

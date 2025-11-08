package ticket.reserve.payment.client.dto;

import java.time.OffsetDateTime;

public record TossResponseDto(
        String paymentKey,
        String status,
        String orderId,
        String orderName,
        OffsetDateTime requestedAt,
        OffsetDateTime approvedAt,
        int totalAmount,
        String method
) {
}

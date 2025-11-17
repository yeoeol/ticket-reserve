package ticket.reserve.payment.application.dto.response;

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

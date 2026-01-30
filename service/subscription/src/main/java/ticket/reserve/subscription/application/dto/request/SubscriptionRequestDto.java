package ticket.reserve.subscription.application.dto.request;

import java.time.LocalDateTime;

public record SubscriptionRequestDto(
        Long buskingId,
        Long userId,
        LocalDateTime startTime
) {
}

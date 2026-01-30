package ticket.reserve.subscription.application.dto.response;

import java.time.LocalDateTime;

public record BuskingNotificationTarget(
        Long buskingId,
        LocalDateTime startTime
) {
}

package ticket.reserve.subscription.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BuskingNotificationTarget(
        Long buskingId,
        LocalDateTime startTime
) {
    public static BuskingNotificationTarget of(Long buskingId, LocalDateTime startTime) {
        return BuskingNotificationTarget.builder()
                .buskingId(buskingId)
                .startTime(startTime)
                .build();
    }
}

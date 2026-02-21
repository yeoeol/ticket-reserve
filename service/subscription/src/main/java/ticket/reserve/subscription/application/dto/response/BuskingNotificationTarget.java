package ticket.reserve.subscription.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BuskingNotificationTarget(
        Long buskingId,
        String title,
        String location,
        LocalDateTime startTime
) {
    public static BuskingNotificationTarget of(Long buskingId, String title, String location, LocalDateTime startTime) {
        return BuskingNotificationTarget.builder()
                .buskingId(buskingId)
                .title(title)
                .location(location)
                .startTime(startTime)
                .build();
    }
}

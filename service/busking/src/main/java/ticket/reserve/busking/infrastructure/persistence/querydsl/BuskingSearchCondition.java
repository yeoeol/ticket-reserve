package ticket.reserve.busking.infrastructure.persistence.querydsl;

import java.time.LocalDateTime;

public record BuskingSearchCondition(
        String title,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}

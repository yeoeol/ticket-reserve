package ticket.reserve.subscription.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SubscriptionRequestDto(
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long userId,
        @NotNull(message = "{busking.startTime.not_null}")
        LocalDateTime startTime
) {
}

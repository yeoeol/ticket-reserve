package ticket.reserve.subscription.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubscriptionCancelRequestDto(
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long userId
) {
}

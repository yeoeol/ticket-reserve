package ticket.reserve.busking.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record IsSubscribeRequestDto(
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long userId
) {
}

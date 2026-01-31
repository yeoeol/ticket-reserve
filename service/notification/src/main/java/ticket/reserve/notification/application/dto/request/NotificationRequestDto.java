package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRequestDto(
        @NotBlank(message = "{notification.title.not_blank}")
        String title,
        @NotBlank(message = "{notification.body.not_blank}")
        String body,
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long receiverId,
        int retryCount
) {
}

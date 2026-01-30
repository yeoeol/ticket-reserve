package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRetryDto(
        @NotBlank(message = "{notification.title.not_blank}")
        String title,
        @NotBlank(message = "{notification.message.not_blank}")
        String message,
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long receiverId,
        int retryCount
) {
    public static NotificationRetryDto from(Notification notification, int currentRetryCount) {
        return NotificationRetryDto.builder()
                .title(notification.getTitle())
                .message(notification.getMessage())
                .receiverId(notification.getReceiverId())
                .buskingId(notification.getBuskingId())
                .retryCount(currentRetryCount)
                .build();
    }
}

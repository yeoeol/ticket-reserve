package ticket.reserve.notification.application.dto.response;

import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

@Builder
public record NotificationResponseDto(
        Long notificationId,
        String title,
        String message,
        Long receiverId,
        NotificationStatus status,
        int retryCount
) {
    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto
                .builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .message(notification.getBody())
                .receiverId(notification.getReceiverId())
                .status(notification.getStatus())
                .retryCount(notification.getRetryCount())
                .build();
    }
}

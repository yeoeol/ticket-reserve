package ticket.reserve.notification.application.dto.response;

import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationResponseDto(
        Long notificationId,
        String title,
        String message,
        Long buskingId,
        Long receiverId,
        NotificationResult result
) {
    public static NotificationResponseDto from(Notification notification, NotificationResult result) {
        return NotificationResponseDto
                .builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .buskingId(notification.getBuskingId())
                .receiverId(notification.getReceiverId())
                .result(result)
                .build();
    }
}

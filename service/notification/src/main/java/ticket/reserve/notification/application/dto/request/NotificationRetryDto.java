package ticket.reserve.notification.application.dto.request;

import lombok.Builder;
import ticket.reserve.notification.domain.Notification;

@Builder
public record NotificationRetryDto(
        String title,
        String message,
        Long receiverId,
        Long buskingId,
        long timestamp
) {
    public static NotificationRetryDto from(Notification notification) {
        return NotificationRetryDto.builder()
                .title(notification.getTitle())
                .message(notification.getMessage())
                .receiverId(notification.getReceiverId())
                .buskingId(notification.getBuskingId())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}

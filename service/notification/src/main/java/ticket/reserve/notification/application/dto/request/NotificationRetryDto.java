package ticket.reserve.notification.application.dto.request;

import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRetryDto(
        String title,
        String message,
        Long receiverId,
        Long buskingId
) {
    public static NotificationRetryDto from(Notification notification) {
        return NotificationRetryDto.builder()
                .title(notification.getTitle())
                .message(notification.getMessage())
                .receiverId(notification.getReceiverId())
                .buskingId(notification.getBuskingId())
                .build();
    }
}

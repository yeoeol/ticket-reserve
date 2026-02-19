package ticket.reserve.notification.application.dto.response;

import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

@Builder
public record NotificationHistoryResponseDto(
        Long id,
        String title,
        String body,
        Long receiverId,
        Long buskingId,
        NotificationStatus status
) {
    public static NotificationHistoryResponseDto from(Notification notification) {
        return NotificationHistoryResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .receiverId(notification.getReceiverId())
                .buskingId(notification.getBuskingId())
                .status(notification.getStatus())
                .build();
    }
}

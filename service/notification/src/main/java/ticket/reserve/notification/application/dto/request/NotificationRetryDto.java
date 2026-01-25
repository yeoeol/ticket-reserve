package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRetryDto(
        @NotBlank(message = "알림 제목은 필수입니다.")
        String title,
        @NotBlank(message = "알림 내용은 필수입니다.")
        String message,
        @NotNull(message = "버스킹ID는 필수입니다.")
        Long receiverId,
        @NotNull(message = "사용자ID는 필수입니다.")
        Long buskingId,
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

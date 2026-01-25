package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRequestDto(
        @NotBlank(message = "알림 제목은 필수입니다.")
        String title,
        @NotBlank(message = "알림 내용은 필수입니다.")
        String message,
        @NotNull(message = "버스킹ID는 필수입니다.")
        Long buskingId,
        @NotNull(message = "사용자ID는 필수입니다.")
        Long receiverId,
        int retryCount
) {
    public static NotificationRequestDto from(NotificationRetryDto retryDto) {
        return NotificationRequestDto.builder()
                .title(retryDto.title())
                .message(retryDto.message())
                .buskingId(retryDto.buskingId())
                .receiverId(retryDto.receiverId())
                .retryCount(retryDto.retryCount())
                .build();
    }

    public Notification toEntity(IdGenerator idGenerator) {
        return Notification.create(
                idGenerator,
                this.title,
                this.message,
                this.buskingId,
                this.receiverId
        );
    }
}

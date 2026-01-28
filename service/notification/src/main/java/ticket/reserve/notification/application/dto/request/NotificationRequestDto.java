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
        @NotBlank(message = "{notification.message.not_blank}")
        String message,
        @NotNull(message = "{notification.buskingId.not_blank}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long receiverId,
        int retryCount
) {
    public static NotificationRequestDto notifyBuskingCreated(String title, String location, Long buskingId, Long receiverId) {
        String message = location + " : 새로운 버스킹이 등록되었습니다.";
        return NotificationRequestDto.builder()
                .title(title)
                .message(message)
                .buskingId(buskingId)
                .receiverId(receiverId)
                .retryCount(0)
                .build();
    }

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

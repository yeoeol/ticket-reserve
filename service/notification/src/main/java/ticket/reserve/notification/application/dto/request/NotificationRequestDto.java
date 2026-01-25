package ticket.reserve.notification.application.dto.request;

import lombok.Builder;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.notification.Notification;

@Builder
public record NotificationRequestDto(
        String title,
        String message,
        Long buskingId,
        Long receiverId
) {
    public static NotificationRequestDto from(NotificationRetryDto retryDto) {
        return NotificationRequestDto.builder()
                .title(retryDto.title())
                .message(retryDto.message())
                .buskingId(retryDto.buskingId())
                .receiverId(retryDto.receiverId())
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

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
    public static NotificationRequestDto from(Notification notification) {
        return NotificationRequestDto.builder()
                .title(notification.getTitle())
                .message(notification.getMessage())
                .buskingId(notification.getBuskingId())
                .receiverId(notification.getReceiverId())
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

package ticket.reserve.notification.application.dto.request;

import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.Notification;

public record NotificationRequestDto(
        String title,
        String message,
        Long buskingId,
        Long receiverId
) {
    public Notification toEntity(IdGenerator idGenerator) {
        return Notification.create(idGenerator, this.title, this.message, this.buskingId, this.receiverId);
    }
}

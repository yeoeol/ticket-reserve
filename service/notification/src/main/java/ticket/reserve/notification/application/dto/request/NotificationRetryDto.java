package ticket.reserve.notification.application.dto.request;

public record NotificationRetryDto(
        String title,
        String message,
        Long receiverId,
        Long buskingId,
        long timestamp
) {
}

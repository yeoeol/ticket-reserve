package ticket.reserve.notification.application.dto.response;

public record NotificationResult(
        boolean isSuccess,
        String errorCode
) {
}

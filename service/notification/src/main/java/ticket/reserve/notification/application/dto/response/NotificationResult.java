package ticket.reserve.notification.application.dto.response;

public record NotificationResult(
        boolean isSuccess,
        Integer errorCode
) {
    public static NotificationResult acceptResult() {
        return new NotificationResult(true, null);
    }

    public static NotificationResult successResult() {
        return new NotificationResult(true, null);
    }

    public static NotificationResult failResult(int errorCode) {
        return new NotificationResult(false, errorCode);
    }
}

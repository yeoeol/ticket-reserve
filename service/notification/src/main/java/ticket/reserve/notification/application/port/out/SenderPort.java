package ticket.reserve.notification.application.port.out;

import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.application.dto.response.NotificationResult;

public interface SenderPort {
    NotificationResult send(Notification notification, String fcmToken);
}

package ticket.reserve.notification.infrastructure.sender;

import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.Notification;

public interface NotificationSender extends SenderPort {
    NotificationResult send(Notification notification);
}

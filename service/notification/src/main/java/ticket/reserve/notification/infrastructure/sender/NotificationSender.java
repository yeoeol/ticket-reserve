package ticket.reserve.notification.infrastructure.sender;

import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;

import java.util.concurrent.CompletableFuture;

public interface NotificationSender extends SenderPort {
    CompletableFuture<NotificationResult> send(Notification notification, String fcmToken);
}

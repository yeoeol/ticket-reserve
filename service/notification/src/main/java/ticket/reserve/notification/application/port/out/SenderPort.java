package ticket.reserve.notification.application.port.out;

import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.application.dto.response.NotificationResult;

import java.util.concurrent.CompletableFuture;

public interface SenderPort {
    CompletableFuture<NotificationResult> send(Notification notification, String fcmToken);
}

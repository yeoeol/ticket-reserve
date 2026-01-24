package ticket.reserve.notification.infrastructure.sender.impl;

import org.springframework.stereotype.Component;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.domain.Notification;
import ticket.reserve.notification.infrastructure.sender.NotificationSender;

@Component
public class FakeNotificationSender implements NotificationSender {

    @Override
    public NotificationResult send(Notification notification) {
        return new NotificationResult(true, null);
    }
}

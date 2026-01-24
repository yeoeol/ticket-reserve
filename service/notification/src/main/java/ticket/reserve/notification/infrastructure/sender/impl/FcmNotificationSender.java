package ticket.reserve.notification.infrastructure.sender.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.infrastructure.sender.NotificationSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmNotificationSender implements NotificationSender {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public NotificationResult send(Notification notification) {
        send(notification.getTitle(), notification.getMessage(), notification.)
    }
}

package ticket.reserve.notification.infrastructure.sender.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public NotificationResult send(Notification notification, String fcmToken) {
        return send(createMessage(notification.getTitle(), notification.getMessage(), notification.getBuskingId(), fcmToken));
    }

    private NotificationResult send(Message message) {
        try {
            String response = firebaseMessaging.send(message);
            log.info("[FcmNotificationSender.send] Successfully Send Fcm Notification: {}", response);
            return NotificationResult.successResult();
        } catch (FirebaseMessagingException e) {
            log.error("[FcmNotificationSender.send] Fail To Send Fcm Notification: {}", e.getMessage(), e);
            return NotificationResult.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private Message createMessage(String title, String message, Long buskingId, String fcmToken) {
        return Message.builder()
                .putData("title", title)
                .putData("message", message)
                .putData("buskingId", String.valueOf(buskingId))
                .setToken(fcmToken)
                .build();
    }
}

package ticket.reserve.notification.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSentEventPayload;
import ticket.reserve.notification.application.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationSentEventHandler implements EventHandler<SubscriptionNotificationSentEventPayload> {

    private final NotificationService notificationService;

    @Override
    public void handle(Event<SubscriptionNotificationSentEventPayload> event) {
        SubscriptionNotificationSentEventPayload payload = event.getPayload();
        if (payload.getUserIds().isEmpty()) return;

        String title = "버스킹 시작 전 알림";
        String body = generateBody(payload);
        notificationService.sendBulkNotification(
                title, body, payload.getBuskingId(), payload.getUserIds()
        );

        log.info("[SubscriptionNotificationSentEventHandler.handle] 버스킹 시작 전 알림 발송 " +
                "- buskingId={} : 총 알림 {}건", payload.getBuskingId(), payload.getUserIds().size());
    }

    private String generateBody(SubscriptionNotificationSentEventPayload payload) {
        return "[구독 알림] %s이(가) %d분 후 시작합니다!"
                .formatted(payload.getTitle(), payload.getRemainingMinutes());
    }

    @Override
    public boolean supports(Event<SubscriptionNotificationSentEventPayload> event) {
        return EventType.SUBSCRIPTION_NOTIFICATION_SENT == event.getType();
    }
}

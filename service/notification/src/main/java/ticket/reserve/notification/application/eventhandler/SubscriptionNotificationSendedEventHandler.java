package ticket.reserve.notification.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSendedEventPayload;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationSendedEventHandler implements EventHandler<SubscriptionNotificationSendedEventPayload> {

    private final NotificationService notificationService;

    @Override
    public void handle(Event<SubscriptionNotificationSendedEventPayload> event) {
        SubscriptionNotificationSendedEventPayload payload = event.getPayload();

        Long buskingId = payload.getBuskingId();
        Set<Long> userIds = payload.getUserIds();
        long remainingMinutes = payload.getRemainingMinutes();

        List<NotificationResponseDto> notificationResponses =
                notificationService.bulkCreateAndSend(buskingId, userIds, remainingMinutes);

        int successCount = notificationResponses.size();
        int failCount = userIds.size() - successCount;

        log.info("[SubscriptionNotificationSendedEventHandler.handle] 버스킹 시작 전 알림 발송 " +
                        "- buskingId={} : 총 알림 {}건 : 성공 {}건, 실패 {}건",
                buskingId, userIds.size(), successCount, failCount);
    }

    @Override
    public boolean supports(Event<SubscriptionNotificationSendedEventPayload> event) {
        return EventType.SUBSCRIPTION_NOTIFICATION_SENDED == event.getType();
    }
}

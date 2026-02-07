package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSentEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationPublishService {

    private final OutboxEventPublisher outboxEventPublisher;
    private final SubscriptionService subscriptionService;

    @Transactional
    public void publishNotificationEvent(Long buskingId, Set<Long> userIds, long remainingMinutes) {
        outboxEventPublisher.publish(
                EventType.SUBSCRIPTION_NOTIFICATION_SENT,
                SubscriptionNotificationSentEventPayload.builder()
                        .buskingId(buskingId)
                        .userIds(userIds)
                        .remainingMinutes(remainingMinutes)
                        .build(),
                buskingId
        );
        subscriptionService.notified(buskingId, userIds);
    }
}

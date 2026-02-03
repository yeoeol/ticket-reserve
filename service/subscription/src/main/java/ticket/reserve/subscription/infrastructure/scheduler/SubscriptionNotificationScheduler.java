package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSentEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.infrastructure.persistence.RedisAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final OutboxEventPublisher outboxEventPublisher;
    private final RedisAdapter redisAdapter;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        Set<BuskingNotificationTarget> targets = redisAdapter.findBuskingIdsReadyToNotify(oneHourLater);
        if (targets == null || targets.isEmpty()) return;

        for (BuskingNotificationTarget target : targets) {
            long remainingMinutes = getRemainingMinutes(now, target.startTime());

            Set<Long> userIds = redisAdapter.findSubscribersByBuskingId(target.buskingId());
            if (userIds == null || userIds.isEmpty()) continue;

            Long buskingId = target.buskingId();
            outboxEventPublisher.publish(
                    EventType.SUBSCRIPTION_NOTIFICATION_SENT,
                    SubscriptionNotificationSentEventPayload.builder()
                            .buskingId(buskingId)
                            .userIds(userIds)
                            .remainingMinutes(remainingMinutes)
                            .build(),
                    buskingId
            );

            redisAdapter.removeSubscriptionData(buskingId);
        }
    }

    private long getRemainingMinutes(LocalDateTime now, LocalDateTime startTime) {
        return Duration.between(now, startTime).toMinutes();
    }
}

package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSendedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.infrastructure.persistence.RedisRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final OutboxEventPublisher outboxEventPublisher;
    private final RedisRepository redisRepository;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        Set<BuskingNotificationTarget> targets = redisRepository.findBuskingIdsReadyToNotify(oneHourLater);
        if (targets == null || targets.isEmpty()) return;


        for (BuskingNotificationTarget target : targets) {
            long remainingMinutes = getRemainingMinutes(now, target.startTime());

            Set<Long> userIds = redisRepository.findSubscribersByBuskingId(target.buskingId());
            if (userIds == null || userIds.isEmpty()) return;

            Long buskingId = target.buskingId();
            outboxEventPublisher.publish(
                    EventType.SUBSCRIPTION_NOTIFICATION_SENDED,
                    SubscriptionNotificationSendedEventPayload.builder()
                            .buskingId(buskingId)
                            .userIds(userIds)
                            .remainingMinutes(remainingMinutes)
                            .build(),
                    buskingId
            );

            redisRepository.removeSubscriptionData(buskingId);
        }
    }

    private long getRemainingMinutes(LocalDateTime now, LocalDateTime startTime) {
        return Duration.between(now, startTime).toMinutes();
    }
}

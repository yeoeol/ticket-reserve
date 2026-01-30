package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubScriptionNotificationSendedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ticket.reserve.subscription.global.util.TimeConverterUtil.convertToMilli;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxEventPublisher outboxEventPublisher;

    @Value("${app.redis.subscription-key:busking:subscription}")
    private String subscriptionKey;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        Set<String> targets = redisTemplate.opsForZSet().rangeByScore(
                subscriptionKey, convertToMilli(now), convertToMilli(oneHourLater)
        );
        if (targets == null) return;

        for (String target : targets) {
            String[] split = target.split(":");
            Long userId = Long.valueOf(split[0]);
            Long buskingId = Long.valueOf(split[1]);

            outboxEventPublisher.publish(
                    EventType.SUBSCRIPTION_NOTIFICATION_SENDED,
                    SubScriptionNotificationSendedEventPayload.builder()
                            .buskingId(buskingId)
                            .userId(userId)
                            .build(),
                    buskingId
            );
            redisTemplate.opsForZSet().remove(subscriptionKey, target);
        }
    }
}

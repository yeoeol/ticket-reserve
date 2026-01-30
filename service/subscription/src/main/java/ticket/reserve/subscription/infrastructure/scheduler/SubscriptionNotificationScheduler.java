package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSendedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ticket.reserve.subscription.global.util.TimeConverterUtil.convertToMilli;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxEventPublisher outboxEventPublisher;

    @Value("${app.redis.notification-schdule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-subscribers-key:busking:subscribers}")
    private String subscribersByBuskingIdKey;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);


        Set<ZSetOperations.TypedTuple<String>> buskingTuples = redisTemplate.opsForZSet().rangeByScoreWithScores(
                notificationScheduleKey, 0, convertToMilli(oneHourLater)
        );
        if (buskingTuples == null || buskingTuples.isEmpty()) return;

        for (ZSetOperations.TypedTuple<String> tuple : buskingTuples) {
            String buskingIdStr = tuple.getValue();
            Long buskingId = Long.valueOf(buskingIdStr);

            Long startTimeMillis = tuple.getScore().longValue();
            LocalDateTime startTime = TimeConverterUtil.convertToLocalDateTime(startTimeMillis);

            long remainingMinutes = getRemainingMinutes(now, startTime);

            Set<String> userIdStrSet = redisTemplate.opsForSet()
                    .members(generateSubscribersByBuskingIdKey(buskingId));
            if (userIdStrSet == null || userIdStrSet.isEmpty()) return;

            Set<Long> userIds = userIdStrSet.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());

            outboxEventPublisher.publish(
                    EventType.SUBSCRIPTION_NOTIFICATION_SENDED,
                    SubscriptionNotificationSendedEventPayload.builder()
                            .buskingId(buskingId)
                            .userIds(userIds)
                            .remainingMinutes(remainingMinutes)
                            .build(),
                    buskingId
            );

            redisTemplate.opsForZSet().remove(notificationScheduleKey, buskingIdStr);
            redisTemplate.delete(generateSubscribersByBuskingIdKey(buskingId));
        }
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }

    private long getRemainingMinutes(LocalDateTime now, LocalDateTime startTime) {
        return Duration.between(now, startTime).toMinutes();
    }
}

package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.subscription.application.port.out.RedisPort;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RedisRepository implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schdule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-subscribers-key:busking:subscribers}")
    private String subscribersByBuskingIdKey;

    public void addToSubscriptionQueue(Long buskingId, Long userId, LocalDateTime startTime) {
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);

        redisTemplate.opsForZSet().add(notificationScheduleKey, String.valueOf(buskingId), startTimeMillis);
        redisTemplate.opsForSet().add(generateSubscribersByBuskingIdKey(buskingId), String.valueOf(userId));
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }
}

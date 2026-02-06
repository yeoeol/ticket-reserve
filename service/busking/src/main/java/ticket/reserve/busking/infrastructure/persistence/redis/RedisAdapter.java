package ticket.reserve.busking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.busking.application.port.out.RedisPort;

@Repository
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-subscribers-key:busking:subscribers}")
    private String subscribersByBuskingIdKey;

    public boolean isSubscribed(Long buskingId, Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet()
                .isMember(generateSubscribersByBuskingIdKey(buskingId), String.valueOf(userId)));
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }
}
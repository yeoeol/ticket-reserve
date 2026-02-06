package ticket.reserve.busking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.busking.application.port.out.RedisPort;
import ticket.reserve.busking.util.TimeConverterUtil;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-subscribers-key:busking:subscribers}")
    private String subscribersByBuskingIdKey;

    @Override
    public boolean isSubscribed(Long buskingId, Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet()
                .isMember(generateSubscribersByBuskingIdKey(buskingId), String.valueOf(userId)));
    }

    // ZSet : 알림 대상 버스킹ID 집합
    @Override
    public void addToNotificationSchedule(Long buskingId, LocalDateTime startTime) {
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);
        redisTemplate.opsForZSet().add(notificationScheduleKey, String.valueOf(buskingId), startTimeMillis);
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }
}
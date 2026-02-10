package ticket.reserve.busking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.busking.application.port.out.NotificationSchedulePort;
import ticket.reserve.busking.util.TimeConverterUtil;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleRedisAdapter implements NotificationSchedulePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    // ZSet : 알림 대상 버스킹ID 집합
    @Override
    public void addToNotificationSchedule(Long buskingId, LocalDateTime startTime, LocalDateTime endTime) {
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);
        redisTemplate.opsForZSet().add(notificationScheduleKey, String.valueOf(buskingId), startTimeMillis);
    }
}
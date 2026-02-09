package ticket.reserve.busking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.busking.application.port.out.RedisPort;
import ticket.reserve.busking.util.TimeConverterUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-detail-key:busking:details}")
    private String buskingDetailsKey;

    // ZSet : 알림 대상 버스킹ID 집합
    @Override
    public void addToNotificationSchedule(Long buskingId, double lat, double lng, LocalDateTime startTime, LocalDateTime endTime) {
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);
        redisTemplate.opsForZSet().add(notificationScheduleKey, String.valueOf(buskingId), startTimeMillis);

        Map<String, String> details = new HashMap<>();
        details.put("lat", String.valueOf(lat));
        details.put("lng", String.valueOf(lng));

        redisTemplate.opsForHash().putAll(generateDetailsKey(buskingId), details);

        // 알림을 보낸 후 예상치 않게 삭제되지 않은 데이터의 만료 시간 설정
        redisTemplate.expireAt(generateDetailsKey(buskingId), endTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String generateDetailsKey(Long buskingId) {
        return buskingDetailsKey + buskingId;
    }
}
package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ticket.reserve.subscription.global.util.TimeConverterUtil.convertToLocalDateTime;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleRedisAdapter implements NotificationSchedulePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    public Set<BuskingNotificationTarget> findTargetsToNotify(LocalDateTime time) {
        long maxScore = TimeConverterUtil.convertToMilli(time);
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(notificationScheduleKey, 0, maxScore);
        if (results == null) return Collections.emptySet();

        return results.stream()
                .filter(NotificationScheduleRedisAdapter::isNotNull)
                .map(tuple -> BuskingNotificationTarget.of(
                        Long.valueOf(tuple.getValue()),
                        convertToLocalDateTime(tuple.getScore().longValue())
                ))
                .collect(Collectors.toSet());
    }

    public void removeFromNotificationSchedule(Long buskingId) {
        redisTemplate.opsForZSet().remove(notificationScheduleKey, String.valueOf(buskingId));
    }

    private static boolean isNotNull(ZSetOperations.TypedTuple<String> tuple) {
        return !(tuple.getValue() == null || tuple.getScore() == null);
    }
}

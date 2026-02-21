package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ticket.reserve.subscription.global.util.TimeConverterUtil.convertToLocalDateTime;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationScheduleRedisAdapter implements NotificationSchedulePort {

    private final BuskingInfoPort buskingInfoPort;
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
                .map(tuple -> {
                    Long buskingId = Long.valueOf(tuple.getValue());
                    BuskingNotificationTarget target = buskingInfoPort.read(buskingId);
                    if (target == null) {
                        log.warn("[NotificationScheduleRedisAdapter.findTargetsToNotify] " +
                                    "buskingId={} 버스킹 정보를 찾을 수 없습니다.", buskingId);
                        return null;
                    }

                    return BuskingNotificationTarget.of(
                            buskingId,
                            target.title(),
                            target.location(),
                            convertToLocalDateTime(tuple.getScore().longValue())
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void removeFromNotificationSchedule(Long buskingId) {
        redisTemplate.opsForZSet().remove(notificationScheduleKey, String.valueOf(buskingId));
    }

    private static boolean isNotNull(ZSetOperations.TypedTuple<String> tuple) {
        return !(tuple.getValue() == null || tuple.getScore() == null);
    }
}

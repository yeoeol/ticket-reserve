package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.RedisPort;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ticket.reserve.subscription.global.util.TimeConverterUtil.convertToLocalDateTime;

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

    public Set<BuskingNotificationTarget> findBuskingIdsReadyToNotify(LocalDateTime time) {
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(notificationScheduleKey, 0, TimeConverterUtil.convertToMilli(time));
        if (results == null) return Collections.emptySet();

        return results.stream()
                .map(tuple -> new BuskingNotificationTarget(
                        Long.valueOf(tuple.getValue()),
                        convertToLocalDateTime(tuple.getScore().longValue())
                ))
                .collect(Collectors.toSet());
    }

    public Set<Long> findSubscribersByBuskingId(Long buskingId) {
        Set<String> userIds = redisTemplate.opsForSet()
                .members(generateSubscribersByBuskingIdKey(buskingId));
        if (userIds == null) return Collections.emptySet();

        return userIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    public void removeSubscriptionData(Long buskingId) {
        redisTemplate.opsForZSet().remove(notificationScheduleKey, String.valueOf(buskingId));
        redisTemplate.delete(generateSubscribersByBuskingIdKey(buskingId));
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }
}

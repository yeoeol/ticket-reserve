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
public class RedisAdapter implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.notification-schedule-key:busking:notification_schedule}")
    private String notificationScheduleKey;

    @Value("${app.redis.busking-subscribers-key:busking:subscribers}")
    private String subscribersByBuskingIdKey;

    // ZSet : 알림 대상 버스킹ID 집합, Set : 특정 버스킹ID를 구독한 사용자ID 집합
    @Override
    public void addToSubscriptionQueue(Long buskingId, Long userId, LocalDateTime startTime) {
        addToNotificationSchedule(buskingId, startTime);
        addToSubscriberByBusking(buskingId, userId);
    }

    @Override
    public void addToNotificationSchedule(Long buskingId, LocalDateTime startTime) {
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);
        redisTemplate.opsForZSet().add(notificationScheduleKey, String.valueOf(buskingId), startTimeMillis);
    }

    public void addToSubscriberByBusking(Long buskingId, Long userId) {
        redisTemplate.opsForSet().add(generateSubscribersByBuskingIdKey(buskingId), String.valueOf(userId));
    }

    @Override
    public void removeFromSubscriptionQueue(Long buskingId, Long userId) {
        redisTemplate.opsForSet().remove(generateSubscribersByBuskingIdKey(buskingId), String.valueOf(userId));
    }

    // 알림 대상 버스킹ID 집합 추출
    public Set<BuskingNotificationTarget> findBuskingIdsReadyToNotify(LocalDateTime time) {
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(notificationScheduleKey, 0, TimeConverterUtil.convertToMilli(time));
        if (results == null) return Collections.emptySet();

        return results.stream()
                .filter(tuple -> isNotNull(tuple))
                .map(tuple -> BuskingNotificationTarget.of(
                        Long.valueOf(tuple.getValue()),
                        convertToLocalDateTime(tuple.getScore().longValue())
                )).collect(Collectors.toSet());
    }

    // 특정 버스킹ID를 구독한 사용자ID 집합 추출
    public Set<Long> findSubscribersByBuskingId(Long buskingId) {
        Set<String> userIds = redisTemplate.opsForSet()
                .members(generateSubscribersByBuskingIdKey(buskingId));
        if (userIds == null) return Collections.emptySet();

        return userIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    // 특정 버스킹ID에 대한 데이터 모두 삭제
    public void removeSubscriptionData(Long buskingId) {
        removeFromNotificationSchedule(buskingId);
        removeSubscriber(buskingId);
    }

    // 알림 스케줄 데이터 삭제
    public void removeFromNotificationSchedule(Long buskingId) {
        redisTemplate.opsForZSet().remove(notificationScheduleKey, String.valueOf(buskingId));
    }

    // 특정 버스킹ID에 대한 구독 데이터 삭제
    public void removeSubscriber(Long buskingId) {
        redisTemplate.delete(generateSubscribersByBuskingIdKey(buskingId));
    }

    private static boolean isNotNull(ZSetOperations.TypedTuple<String> tuple) {
        return !(tuple.getValue() == null || tuple.getScore() == null);
    }

    private String generateSubscribersByBuskingIdKey(Long buskingId) {
        return subscribersByBuskingIdKey + ":" + buskingId;
    }
}

package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.subscription.application.NotificationPublishService;
import ticket.reserve.subscription.application.SubscriptionService;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.infrastructure.persistence.RedisAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final NotificationPublishService notificationPublishService;
    private final SubscriptionService subscriptionService;
    private final RedisAdapter redisAdapter;


    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();

        Set<BuskingNotificationTarget> targets = findTargets(now);
        if (targets == null || targets.isEmpty()) return;

        for (BuskingNotificationTarget target : targets) {
            long remainingMinutes = getRemainingMinutes(now, target.startTime());
            if (remainingMinutes <= -10) {
                // 다시 알림이 가지 않도록 알림 스케줄링 목록에서 제거
                redisAdapter.removeFromNotificationSchedule(target.buskingId());
                continue;
            }

            Map<Object, Object> details = redisAdapter.findEntries(target.buskingId());
            if (details.isEmpty()) continue;

            Double lat = Double.valueOf((String) details.get("lat"));
            Double lng = Double.valueOf((String) details.get("lng"));

            // 반경 5km 이내 사용자 ID 조회
            Set<Long> nearbyUserIds = redisAdapter.findNearbyUsers(lat, lng);
            if (nearbyUserIds == null || nearbyUserIds.isEmpty()) continue;

            // DB에서 알림이 가지 않은 구독자 조회
            Set<Long> targetUserIds = subscriptionService.findSubscribers(target.buskingId(), nearbyUserIds);
            if (targetUserIds == null || targetUserIds.isEmpty()) continue;

            // 알림 발송 이벤트 발행 및 구독 엔티티 알림 여부 변경
            notificationPublishService.publishNotificationEvent(target.buskingId(), targetUserIds, max(0, remainingMinutes));
            // 스케줄링 목록에서 제거 및 버스킹 상세 정보 해쉬에서 제거
            redisAdapter.removeFromNotificationSchedule(target.buskingId());
        }
    }

    // 알림 대상 버스킹 정보 추출
    private Set<BuskingNotificationTarget> findTargets(LocalDateTime now) {
        LocalDateTime oneHourLater = now.plusHours(1);
        return redisAdapter.findBuskingIdsReadyToNotify(oneHourLater);
    }

    private long getRemainingMinutes(LocalDateTime now, LocalDateTime startTime) {
        return Duration.between(now, startTime).toMinutes();
    }
}

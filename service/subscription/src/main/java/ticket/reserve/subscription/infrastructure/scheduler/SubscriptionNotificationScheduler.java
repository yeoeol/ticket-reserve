package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.subscription.application.NotificationPublishService;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.infrastructure.persistence.RedisAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final NotificationPublishService notificationPublishService;
    private final RedisAdapter redisAdapter;

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();

        Set<BuskingNotificationTarget> targets = findTargets(now);
        if (targets == null || targets.isEmpty()) return;

        for (BuskingNotificationTarget target : targets) {
            long remainingMinutes = getRemainingMinutes(now, target.startTime());
            if (remainingMinutes <= -10) continue;

            Set<Long> userIds = redisAdapter.findSubscribersByBuskingId(target.buskingId());
            if (userIds == null || userIds.isEmpty()) continue;

            notificationPublishService.publishNotificationEvent(target.buskingId(), userIds, max(0, remainingMinutes));
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

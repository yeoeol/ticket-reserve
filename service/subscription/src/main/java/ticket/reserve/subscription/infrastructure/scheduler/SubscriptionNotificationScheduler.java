package ticket.reserve.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.subscription.application.NotificationPublishService;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

/**
 * 1분 주기 스케줄링
 * 버스킹 구독자에게 알림 발송 이벤트 발행
 */
@Component
@RequiredArgsConstructor
public class SubscriptionNotificationScheduler {

    private final NotificationPublishService notificationPublishService;
    private final NotificationSchedulePort notificationSchedulePort;
    private final SubscriptionQueryService subscriptionQueryService;


    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void subscriptionNotificationScheduler() {
        LocalDateTime now = LocalDateTime.now();

        Set<BuskingNotificationTarget> targets = findTargets(now);
        if (targets == null || targets.isEmpty()) return;

        for (BuskingNotificationTarget target : targets) {
            long remainingMinutes = getRemainingMinutes(now, target.startTime());

            // DB에서 알림이 가지 않은 구독자 조회
            Set<Long> targetUserIds = subscriptionQueryService.findSubscribers(target.buskingId());
            if (targetUserIds == null || targetUserIds.isEmpty()) continue;

            // 알림 발송 이벤트 발행 및 구독 엔티티 알림 여부 변경
            notificationPublishService.publishNotificationEvent(target.buskingId(), targetUserIds, max(0, remainingMinutes));
            // 스케줄링 목록에서 제거
            notificationSchedulePort.removeFromNotificationSchedule(target.buskingId());
        }
    }

    // 알림 대상 버스킹 정보 추출
    private Set<BuskingNotificationTarget> findTargets(LocalDateTime now) {
        LocalDateTime oneHourLater = now.plusHours(1);
        return notificationSchedulePort.findTargetsToNotify(oneHourLater);
    }

    // 버스킹 시작 시간까지 남은 시간 반환
    private long getRemainingMinutes(LocalDateTime now, LocalDateTime startTime) {
        return Duration.between(now, startTime).toMinutes();
    }
}

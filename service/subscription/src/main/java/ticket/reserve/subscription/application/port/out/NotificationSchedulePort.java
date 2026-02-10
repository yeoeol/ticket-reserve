package ticket.reserve.subscription.application.port.out;

import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;

import java.time.LocalDateTime;
import java.util.Set;

public interface NotificationSchedulePort {
    // 알림을 보낼 버스킹 정보 조회 (score : 0 ~ time)
    Set<BuskingNotificationTarget> findTargetsToNotify(LocalDateTime time);

    // 알림 스케줄에서 삭제
    void removeFromNotificationSchedule(Long buskingId);
}

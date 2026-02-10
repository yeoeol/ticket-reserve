package ticket.reserve.notification.application;

import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

import java.util.List;

public interface NotificationQueryService {
    // 알림 상태 별 조회
    List<Notification> findByStatus(NotificationStatus status);

    // 알림 IN절 조회
    List<Notification> findAllByIds(List<Long> notificationIds);
}

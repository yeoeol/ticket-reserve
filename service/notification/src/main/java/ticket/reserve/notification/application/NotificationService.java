package ticket.reserve.notification.application;

import java.util.Collection;
import java.util.List;

public interface NotificationService {
    // Bulk 알림 전송
    void sendBulkNotification(String title, String body, Long buskingId, Collection<Long> userIds);

    // 재시도 횟수 증가
    void incrementRetryCounts(List<Long> notificationIds);
}

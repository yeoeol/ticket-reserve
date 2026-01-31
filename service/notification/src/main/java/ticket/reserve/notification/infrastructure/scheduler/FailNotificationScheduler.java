package ticket.reserve.notification.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.notification.application.NotificationCrudService;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FailNotificationScheduler {

    private final NotificationCrudService notificationCrudService;
    private final NotificationService notificationService;

    @Scheduled(initialDelay = 10, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationCrudService.findByStatus(NotificationStatus.FAIL);

        Map<Long, List<Notification>> groupedByBuskingId = failedNotifications.stream()
                .collect(Collectors.groupingBy(Notification::getBuskingId));

        groupedByBuskingId.forEach((buskingId, notifications) -> {
            List<Long> userIds = notifications.stream()
                    .map(Notification::getReceiverId)
                    .toList();

            String title = notifications.getFirst().getTitle();
            String body = notifications.getFirst().getBody();
            notificationService.sendBulkNotification(
                    title,
                    body,
                    buskingId,
                    userIds
            );
            notifications.forEach(Notification::incrementRetryCount);
        });
    }
}

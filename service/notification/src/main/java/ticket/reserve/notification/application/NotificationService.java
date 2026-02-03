package ticket.reserve.notification.application;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.repository.BulkNotificationRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final FcmTokenService fcmTokenService;
    private final BulkNotificationRepository bulkNotificationRepository;
    private final NotificationCrudService notificationCrudService;

    public void sendBulkNotification(String title, String body, Long buskingId, Collection<Long> userIds) {
        List<Notification> pendingNotifications = userIds.stream()
                .map(userId -> Notification.create(idGenerator, title, body, userId, buskingId))
                .toList();
        bulkNotificationRepository.bulkInsert(pendingNotifications);

        List<List<Notification>> partitions = Lists.partition(pendingNotifications, 500);

        for (List<Notification> partition : partitions) {
            List<Long> pUserIds = partition.stream()
                    .map(Notification::getReceiverId)
                    .toList();
            List<String> fcmTokens = fcmTokenService.getTokensByUserIds(pUserIds);

            senderPort.send(title, body, buskingId, fcmTokens)
                    .thenAccept(response -> {
                        handleBatchResult(partition, response);
                        bulkNotificationRepository.bulkUpsert(partition);
                    })
                    .exceptionally(ex -> {
                        handleBatchFailure(partition);
                        bulkNotificationRepository.bulkUpsert(partition);
                        return null;
                    });
        }
    }

    @Transactional
    public void incrementRetryCounts(List<Long> notificationIds) {
        List<Notification> notifications = notificationCrudService.findAllByIds(notificationIds);
        notifications.forEach(Notification::incrementRetryCount);
    }

    private void handleBatchResult(List<Notification> partition, NotificationBatchResponseDto response) {
        List<NotificationBatchResponseDto.NotificationSendResponseDto> responses = response.responses();
        for (int i = 0; i < responses.size(); i++) {
            Notification notification = partition.get(i);

            if (responses.get(i).isSuccessful()) {
                notification.markAsSuccess();
            } else {
                notification.markAsFail();
                log.warn("[NotificationService] 알림 발송 실패: 사용자ID={}, {}",
                        notification.getReceiverId(), responses.get(i).errorMessage());
            }
        }
    }

    private void handleBatchFailure(List<Notification> partition) {
        partition.forEach(Notification::markAsFail);
    }
}

package ticket.reserve.notification.application.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.FcmTokenService;
import ticket.reserve.notification.application.NotificationQueryService;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.repository.BulkNotificationRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final FcmTokenService fcmTokenService;
    private final BulkNotificationRepository bulkNotificationRepository;
    private final NotificationQueryService notificationQueryService;

    @Override
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
            Map<Long, String> tokenMap = fcmTokenService.getTokenMapByUserIds(pUserIds);

            // 토큰이 있는 사용자의 알림 엔티티 추출
            List<Notification> sendableNotifications = partition.stream()
                    .filter(notification -> tokenMap.containsKey(notification.getReceiverId()))
                    .toList();

            // 알림을 보낼 FCM TOKEN 리스트 추출
            List<String> fcmTokens = sendableNotifications.stream()
                    .map(notification -> tokenMap.get(notification.getReceiverId()))
                    .toList();

            // 토큰이 없는 알림은 실패 처리
            List<Notification> nonSendable = partition.stream()
                    .filter(notification -> !tokenMap.containsKey(notification.getReceiverId()))
                    .toList();
            handleBatchFailure(nonSendable);

            // 발송할 알림이 없다면 결과 반영 후 continue
            if (sendableNotifications.isEmpty()) {
                bulkNotificationRepository.bulkUpsert(partition);
                continue;
            }

            // 알림 발송 로직 수행
            senderPort.send(title, body, buskingId, fcmTokens)
                    .thenAccept(response -> {
                        handleBatchResult(sendableNotifications, response);
                        bulkNotificationRepository.bulkUpsert(partition);
                    }).exceptionally(ex -> {
                        handleBatchFailure(sendableNotifications);
                        bulkNotificationRepository.bulkUpsert(partition);
                        return null;
                    });
        }
    }

    @Override
    @Transactional
    public void incrementRetryCounts(List<Long> notificationIds) {
        List<Notification> notifications = notificationQueryService.findAllByIds(notificationIds);
        notifications.forEach(Notification::incrementRetryCount);
    }

    private void handleBatchResult(List<Notification> notifications, NotificationBatchResponseDto response) {
        List<NotificationBatchResponseDto.NotificationSendResponseDto> responses = response.responses();
        if (responses.size() != notifications.size()) {
            log.error("[NotificationServiceImpl] 응답 수({})와 파티션 수({}) 불일치", responses.size(), notifications.size());
            handleBatchFailure(notifications);
            return;
        }

        for (int i = 0; i < responses.size(); i++) {
            Notification notification = notifications.get(i);

            if (responses.get(i).isSuccessful()) {
                notification.markAsSuccess();
            } else {
                notification.markAsFail();
                log.warn("[NotificationService] 알림 발송 실패: 사용자ID={}, {}",
                        notification.getReceiverId(), responses.get(i).errorMessage());
            }
        }
    }

    private void handleBatchFailure(List<Notification> notifications) {
        notifications.forEach(Notification::markAsFail);
    }
}

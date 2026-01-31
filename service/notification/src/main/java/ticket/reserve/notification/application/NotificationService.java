package ticket.reserve.notification.application;

import com.google.common.collect.Lists;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.repository.BulkNotificationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final FcmTokenService fcmTokenService;
    private final BulkNotificationRepository bulkNotificationRepository;

    @Transactional
    public void sendBulkNotification(String title, String body, Long buskingId, List<Long> userIds) {
        List<Notification> pendingNotifications = userIds.stream()
                .map(userId -> Notification.create(idGenerator, title, body, userId))
                .toList();
        bulkNotificationRepository.bulkInsert(pendingNotifications);

        List<List<Notification>> partitions = Lists.partition(pendingNotifications, 500);

        for (List<Notification> partition : partitions) {
            List<Long> pUserIds = partition.stream()
                    .map(Notification::getReceiverId)
                    .toList();
            List<String> fcmTokens = fcmTokenService.getTokensByUserIds(pUserIds);

            senderPort.send(title, body, buskingId, fcmTokens)
                    .thenAccept(response -> handleBatchResult(partition, response))
                    .exceptionally(ex -> {
                        handleBatchFailure(partition);
                        return null;
                    });
        }
    }

    private void handleBatchResult(List<Notification> partition, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            Notification notification = partition.get(i);

            SendResponse sendResponse = responses.get(i);
            if (sendResponse.isSuccessful()) {
                notification.markAsSuccess();
            } else {
                notification.markAsFail();
                log.warn("[NotificationService] 알림 발송 실패: 사용자ID={}, {}",
                        notification.getReceiverId(), sendResponse.getException().getMessage());
            }
        }
    }

    private void handleBatchFailure(List<Notification> partition) {
        partition.forEach(Notification::markAsFail);
    }

/*    private void handleFailure(NotificationRetryDto retryDto) {
        int currentRetryCount = retryDto.retryCount();

        if (currentRetryCount >= MAX_RETRY_COUNT) {
            log.error("[NotificationService.createAndSend.handleFailure] 최대 재시도 횟수 초과: buskingId={}, receiverId={}, title={}",
                    retryDto.buskingId(), retryDto.receiverId(), retryDto.title()
            );
            // TODO : 알림 발송 5회 실패한 DLQ(Dead Letter Queue) 구현
            return;
        }

        // 지수 백오프: 2^n * 60초 (1분 -> 2분 -> 4분 -> 8분 -> 16분)
        long nextDelaySeconds = (long) Math.pow(2, Math.max(0, currentRetryCount-1)) * 60;

        redisService.addFailedNotification(retryDto, nextDelaySeconds);
        log.warn("[NotificationService.createAndSend.handleFailure] " +
                        "알림 전송 실패(횟수:{}/{}), {}초 후 재시도: buskingId={}, receiverId={}, title={}",
                currentRetryCount, MAX_RETRY_COUNT, nextDelaySeconds, retryDto.buskingId(), retryDto.receiverId(), retryDto.title()
        );
    }*/
}

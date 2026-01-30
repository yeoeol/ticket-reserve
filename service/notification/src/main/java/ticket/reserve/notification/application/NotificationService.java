package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.repository.BulkNotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;
    private final FcmTokenService fcmTokenService;
    private final BulkNotificationRepository bulkNotificationRepository;

    private static final int MAX_RETRY_COUNT = 5;

    public NotificationResponseDto createAndSend(NotificationRequestDto request) {
        Notification notification = request.toEntity(idGenerator);

        Optional<String> optionalFcmToken = fcmTokenService.getTokenByUserId(request.receiverId());
        // FcmToken이 존재하지 않으면 알림 발송 실패 카운트 증가
        if (optionalFcmToken.isEmpty()) {
            NotificationRetryDto nextRetryDto = NotificationRetryDto.from(notification, request.retryCount() + 1);
            handleFailure(nextRetryDto);
            return NotificationResponseDto.from(notification, NotificationResult.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        String fcmToken = optionalFcmToken.get();
        NotificationResult result = senderPort.send(notification, fcmToken);
        if (result.isSuccess()) {
            notificationCrudService.save(notification);
        } else {
            NotificationRetryDto nextRetryDto = NotificationRetryDto.from(notification, request.retryCount() + 1);
            handleFailure(nextRetryDto);
        }
        return NotificationResponseDto.from(notification, result);
    }

    public List<NotificationResponseDto> bulkCreateAndSend(Long buskingId, Set<Long> userIds, long remainingMinutes) {
        List<Notification> notifications = userIds.stream()
                .map(userId -> Notification.createSubscriptionRemider(idGenerator, buskingId, userId, remainingMinutes))
                .toList();
        bulkNotificationRepository.bulkInsert(notifications);

        for (Notification notification : notifications) {
            Optional<String> optionalFcmToken = fcmTokenService.getTokenByUserId(notification.getReceiverId());
            if (optionalFcmToken.isEmpty()) {
                NotificationRetryDto nextRetryDto = NotificationRetryDto.from(notification, 1);
                handleFailure(nextRetryDto);
                continue;
            }

            String fcmToken = optionalFcmToken.get();
            NotificationResult result = senderPort.send(notification, fcmToken);
            if (!result.isSuccess()) {
                NotificationRetryDto nextRetryDto = NotificationRetryDto.from(notification, 1);
                handleFailure(nextRetryDto);
            }
        }

        return notifications.stream()
                .map(notification -> NotificationResponseDto.from(notification, NotificationResult.acceptResult()))
                .toList();
    }

    private void handleFailure(NotificationRetryDto retryDto) {
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
    }
}

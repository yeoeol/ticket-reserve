package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;
    private final FcmTokenService fcmTokenService;

    private static final int MAX_RETRY_COUNT = 5;

    public NotificationResponseDto createAndSend(NotificationRequestDto request) {
        Notification notification = request.toEntity(idGenerator);

        String fcmToken = fcmTokenService.getTokenByUserId(request.receiverId());

        NotificationResult result = senderPort.send(notification, fcmToken);
        if (result.isSuccess()) {
            notificationCrudService.save(notification);
        }
        else {
            NotificationRetryDto nextRetryDto = NotificationRetryDto.from(notification, request.retryCount() + 1);
            handleFailure(nextRetryDto);
        }
        return NotificationResponseDto.from(notification, result);
    }

    private void handleFailure(NotificationRetryDto retryDto) {
        int currentRetryCount = retryDto.retryCount();

        if (currentRetryCount >= MAX_RETRY_COUNT) {
            log.error("[NotificationService.createAndSend.handleFailure] 최대 재시도 횟수 초과: receiverId={}, title={}",
                    retryDto.receiverId(), retryDto.title()
            );
            // TODO : 알림 발송 5회 실패한 DLQ(Dead Letter Queue) 구현
            return;
        }

        // 지수 백오프: 2^n * 60초 (1분 -> 2분 -> 4분 -> 8분 -> 16분)
        long nextDelaySeconds = (long) Math.pow(2, currentRetryCount) * 60;

        redisService.addFailedNotification(retryDto, nextDelaySeconds);
        log.warn("[NotificationService.createAndSend.handleFailure] " +
                        "알림 전송 실패(횟수:{}/{}), {}초 후 재시도: receiverId={}, title={}",
                currentRetryCount, MAX_RETRY_COUNT, nextDelaySeconds, retryDto.receiverId(), retryDto.title()
        );
    }
}

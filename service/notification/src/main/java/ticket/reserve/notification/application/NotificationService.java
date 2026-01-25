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

    public NotificationResponseDto createAndSend(NotificationRequestDto request) {
        Notification notification = request.toEntity(idGenerator);

        String fcmToken = fcmTokenService.getTokenByUserId(request.receiverId());

        NotificationResult result = senderPort.send(notification, fcmToken);
        if (result.isSuccess()) {
            notificationCrudService.save(notification);
        }
        else {
            redisService.addFailedNotification(NotificationRetryDto.from(notification));
            log.warn("[NotificationService.createAndSend] 알림 전송 실패, 재시도 큐에 추가: receiverId={}, title={}, errorCode={}",
                    notification.getReceiverId(), notification.getTitle(), result.errorCode());
        }
        return NotificationResponseDto.from(notification, result);
    }
}

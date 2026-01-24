package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.Notification;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final SenderPort senderPort;
    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;

    public NotificationResponseDto createAndSend(NotificationRequestDto request) {
        Notification notification = request.toEntity(idGenerator);

        try {
            senderPort.send(notification);
            notificationCrudService.save(notification);
        } catch (Exception e) {
            redisService.addFailedNotification(NotificationRetryDto.from(notification));
        }
        return NotificationResponseDto.from(notification);
    }
}

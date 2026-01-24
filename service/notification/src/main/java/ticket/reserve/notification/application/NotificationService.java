package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.Notification;
import ticket.reserve.notification.domain.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final IdGenerator idGenerator;
    private final NotificationRepository notificationRepository;
    private final SenderPort senderPort;

    @Transactional
    public NotificationResponseDto create(NotificationRequestDto request) {
        Notification notification = request.toEntity(idGenerator);
        notificationRepository.save(notification);
        try {
            NotificationResult result = senderPort.send(notification);
        } catch (Exception e) {
            return null;
        }
        return NotificationResponseDto.from(notification);
    }
}

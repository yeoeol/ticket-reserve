package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;
import ticket.reserve.notification.domain.notification.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationCrudService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }
}

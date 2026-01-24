package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationCrudService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
}

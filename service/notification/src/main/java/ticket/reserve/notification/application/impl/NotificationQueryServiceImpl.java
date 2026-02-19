package ticket.reserve.notification.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.notification.application.NotificationQueryService;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;
import ticket.reserve.notification.domain.notification.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByIds(List<Long> notificationIds) {
        return notificationRepository.findAllById(notificationIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findHistoriesByUserId(Long userId) {
        return notificationRepository.findAllByReceiverId(userId);
    }
}

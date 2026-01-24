package ticket.reserve.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.notification.domain.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long receiverId);
}

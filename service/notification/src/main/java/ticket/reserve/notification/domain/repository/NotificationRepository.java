package ticket.reserve.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

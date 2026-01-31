package ticket.reserve.notification.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long receiverId);

    @Query(value = "SELECT n FROM Notification n WHERE n.status = :status")
    List<Notification> findByStatus(@Param("status") NotificationStatus status);
}

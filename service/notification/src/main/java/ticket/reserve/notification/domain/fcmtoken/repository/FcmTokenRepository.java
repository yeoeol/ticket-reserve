package ticket.reserve.notification.domain.fcmtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
}

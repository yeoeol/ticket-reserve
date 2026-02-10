package ticket.reserve.notification.domain.fcmtoken.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value =
            "SELECT f " +
            "FROM FcmToken f " +
            "WHERE f.userId = :userId")
    Optional<FcmToken> findByUserIdForUpdate(Long userId);

    List<FcmToken> findByUserIdIn(List<Long> userIds);
}

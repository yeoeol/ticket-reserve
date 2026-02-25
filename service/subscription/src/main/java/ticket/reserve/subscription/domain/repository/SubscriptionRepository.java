package ticket.reserve.subscription.domain.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByBuskingIdAndUserId(Long buskingId, Long userId);
    List<Subscription> findAllByBuskingIdAndUserIdIn(Long buskingId, Collection<Long> userIds);
    boolean existsByBuskingIdAndUserIdAndStatus(Long buskingId, Long userId, SubscriptionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value =
            "SELECT s " +
            "FROM Subscription s " +
            "WHERE s.buskingId = :buskingId " +
                    "AND s.userId = :userId")
    Optional<Subscription> findByBuskingIdAndUserIdForUpdate(
            @Param("buskingId") Long buskingId,
            @Param("userId") Long userId
    );

    @Query(value =
            "SELECT DISTINCT s.userId " +
            "FROM Subscription s " +
            "WHERE s.buskingId = :buskingId " +
                    "AND s.status = :status " +
                    "AND s.isNotified = :isNotified")
    Set<Long> findUserIdsByBuskingIdAndStatusAndIsNotified(
            @Param("buskingId") Long buskingId,
            @Param("status") SubscriptionStatus status,
            @Param("isNotified") boolean isNotified
    );

    @Query(value =
            "SELECT s.buskingId " +
            "FROM Subscription s " +
            "WHERE s.userId = :userId " +
                    "AND s.status = :status")
    List<Long> findBuskingIdsByUserIdWithActivated(
            @Param("userId") Long userId,
            @Param("status") SubscriptionStatus status
    );

    @Modifying
    @Query(value =
            "DELETE FROM Subscription s " +
            "WHERE s.buskingId = :buskingId")
    void deleteAllByBuskingId(@Param("buskingId") Long buskingId);
}

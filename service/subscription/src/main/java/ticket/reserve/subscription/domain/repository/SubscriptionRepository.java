package ticket.reserve.subscription.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByBuskingIdAndUserId(Long buskingId, Long userId);

    @Query(value =
            "SELECT DISTINCT s.userId " +
            "FROM Subscription s " +
            "WHERE s.buskingId = :buskingId " +
                    "AND s.status = :status " +
                    "AND s.isNotified = :isNotified")
    Set<Long> findUserIdsByBuskingIdAndStatusAndIsNotified(Long buskingId, SubscriptionStatus status, boolean isNotified);

    List<Subscription> findAllByBuskingIdAndUserIdIn(Long buskingId, Collection<Long> userIds);

    boolean existsByBuskingIdAndUserIdAndStatus(Long buskingId, Long userId, SubscriptionStatus status);

    @Query(value =
            "SELECT s.buskingId " +
            "FROM Subscription s " +
            "WHERE s.userId = :userId " +
                    "AND s.status = :status")
    List<Long> findAllByUserIdWithActivated(Long userId, SubscriptionStatus status);
}

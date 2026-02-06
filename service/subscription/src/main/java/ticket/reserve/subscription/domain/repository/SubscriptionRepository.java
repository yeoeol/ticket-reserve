package ticket.reserve.subscription.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByBuskingIdAndUserId(Long buskingId, Long userId);

    @Query(value =
            "SELECT s " +
            "FROM Subscription s " +
            "WHERE s.buskingId = :buskingId " +
                    "AND s.status = :status " +
                    "AND s.isNotified = :isNotified")
    List<Subscription> findByBuskingIdAndStatusAndIsNotified(Long buskingId, SubscriptionStatus status, boolean isNotified);

    List<Subscription> findAllByUserIdIn(Collection<Long> userIds);

    boolean existsByBuskingIdAndUserIdAndStatus(Long buskingId, Long userId, SubscriptionStatus status);

    List<Subscription> findAllByUserId(Long userId);
}

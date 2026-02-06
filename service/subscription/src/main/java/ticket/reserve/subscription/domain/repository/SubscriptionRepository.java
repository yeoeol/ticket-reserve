package ticket.reserve.subscription.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.subscription.domain.Subscription;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByBuskingIdAndUserId(Long buskingId, Long userId);
}

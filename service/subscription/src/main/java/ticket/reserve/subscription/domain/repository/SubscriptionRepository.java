package ticket.reserve.subscription.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.subscription.domain.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}

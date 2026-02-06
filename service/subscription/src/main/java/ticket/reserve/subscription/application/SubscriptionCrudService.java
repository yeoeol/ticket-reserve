package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionCrudService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

}

package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionCrudService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void cancel(Long buskingId, Long userId) {
        Subscription subscription = subscriptionRepository.findByBuskingIdAndUserId(buskingId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscription.cancel();
    }

    @Transactional(readOnly = true)
    public List<Subscription> findByBuskingIdAndStatusAndNotified(Long buskingId) {
        return subscriptionRepository.findByBuskingIdAndStatusAndIsNotified(buskingId, SubscriptionStatus.ACTIVATED, false);
    }

    @Transactional(readOnly = true)
    public List<Subscription> findAllByUserIds(Set<Long> userIds) {
        return subscriptionRepository.findAllByUserIdIn(userIds);
    }

    @Transactional(readOnly = true)
    public boolean isSubscriptionActive(Long buskingId, Long userId) {
        return subscriptionRepository.existsByBuskingIdAndUserIdAndStatus(
                buskingId, userId, SubscriptionStatus.ACTIVATED
        );
    }
}

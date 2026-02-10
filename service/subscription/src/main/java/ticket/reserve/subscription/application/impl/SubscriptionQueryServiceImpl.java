package ticket.reserve.subscription.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    // 활성 구독 확인 : buskingIds 반환
    @Transactional(readOnly = true)
    public List<Long> findBuskingIdsByUserIdWithActivated(Long userId) {
        return subscriptionRepository.findBuskingIdsByUserIdWithActivated(userId, SubscriptionStatus.ACTIVATED);
    }

    // 활성 구독 확인 : true/false
    @Transactional(readOnly = true)
    public boolean isSubscriptionActive(Long buskingId, Long userId) {
        return subscriptionRepository.existsByBuskingIdAndUserIdAndStatus(
                buskingId, userId, SubscriptionStatus.ACTIVATED
        );
    }

    // 구독자 조회
    @Transactional(readOnly = true)
    public Set<Long> findSubscribers(Long buskingId) {
        return subscriptionRepository.findUserIdsByBuskingIdAndStatusAndIsNotified(
                buskingId, SubscriptionStatus.ACTIVATED, false
        );
    }

    @Transactional(readOnly = true)
    public Subscription getSubscription(Long buskingId, Long userId) {
        return subscriptionRepository.findByBuskingIdAndUserId(buskingId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }
}

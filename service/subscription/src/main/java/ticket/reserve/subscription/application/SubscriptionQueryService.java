package ticket.reserve.subscription.application;

import ticket.reserve.subscription.domain.Subscription;

import java.util.List;
import java.util.Set;

public interface SubscriptionQueryService {
    // 활성 구독 확인 : buskingIds 반환
    List<Long> findBuskingIdsByUserIdWithActivated(Long userId);

    // 활성 구독 확인 : true/false
    boolean isSubscriptionActive(Long buskingId, Long userId);

    // 구독자 조회
    Set<Long> findSubscribers(Long buskingId);

    // 구독 엔티티 조회
    Subscription getSubscription(Long buskingId, Long userId);
}

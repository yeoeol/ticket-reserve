package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;
import ticket.reserve.subscription.application.port.out.BuskingPort;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final IdGenerator idGenerator;
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;
    private final BuskingPort buskingPort;

    // 구독 로직
    @Transactional
    public void subscribe(SubscriptionRequestDto request) {
        Optional<Subscription> optionalSubscription = subscriptionRepository
                .findByBuskingIdAndUserIdForUpdate(request.buskingId(), request.userId());

        // 같은 버스킹에 대해 구독 내역이 있다면 구독 상태만 변경
        if (optionalSubscription.isPresent()) {
            optionalSubscription.get().activate();
            return;
        }

        subscriptionRepository.save(request.toEntity(idGenerator));
    }

    // 구독 취소 로직
    @Transactional
    public void unsubscribe(SubscriptionCancelRequestDto request) {
        Subscription subscription = subscriptionQueryService
                .getSubscription(request.buskingId(), request.userId());
        subscription.cancel();
    }

    // 알림 전송 완료 체크
    @Transactional
    public void notified(Long buskingId, Set<Long> userIds) {
        subscriptionRepository.findAllByBuskingIdAndUserIdIn(buskingId, userIds)
                        .forEach(Subscription::notified);
    }

    // 구독한 버스킹 목록 조회
    public List<BuskingResponseDto> getAllByUserId(Long userId) {
        // userId에 대해 ACTIVATED 상태인 것들의 buskingId를 조회
        List<Long> buskingIds = subscriptionQueryService.findBuskingIdsByUserIdWithActivated(userId);

        return buskingPort.getAllByBuskingIds(buskingIds).stream()
                .map(busking -> busking.withSubscribed(true))
                .toList();
    }

    public Set<Long> findSubscribers(Long buskingId, Set<Long> nearbyUserIds) {
        return subscriptionRepository.findAllByBuskingIdAndUserIdInAndStatusAndIsNotified(
                buskingId, SubscriptionStatus.ACTIVATED, false, nearbyUserIds
        );
    }
}

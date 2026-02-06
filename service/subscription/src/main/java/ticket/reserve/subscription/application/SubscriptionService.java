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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final IdGenerator idGenerator;
    private final SubscriptionCrudService subscriptionCrudService;
    private final BuskingPort buskingPort;

    @Transactional
    public void subscribe(SubscriptionRequestDto request) {
        Optional<Subscription> optionalSubscription = subscriptionCrudService
                .findByBuskingIdAndUserId(request.buskingId(), request.userId());

        // 같은 버스킹에 대해 구독 내역이 있다면 구독 상태만 변경
        optionalSubscription.ifPresentOrElse(
                Subscription::activate,
                () -> subscriptionCrudService.save(request.toEntity(idGenerator))
        );
    }

    public void unsubscribe(SubscriptionCancelRequestDto request) {
        subscriptionCrudService.cancel(request.buskingId(), request.userId());
    }

    public Set<Long> findSubscribers(Long buskingId) {
        List<Subscription> subscriptions = subscriptionCrudService.findByBuskingIdAndStatusAndNotified(buskingId);

        return subscriptions.stream()
                .map(Subscription::getUserId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void notified(Set<Long> userIds) {
        subscriptionCrudService.findAllByUserIds(userIds)
                .forEach(Subscription::notified);
    }

    public boolean isSubscriptionActive(Long buskingId, Long userId) {
        return subscriptionCrudService.isSubscriptionActive(buskingId, userId);
    }

    public List<BuskingResponseDto> getAllByUserId(Long userId) {
        // userId에 대해 ACTIVATED 상태인 것들의 buskingId를 조회
        List<Long> buskingIds = subscriptionCrudService.findBuskingIdsByUserIdWithActivated(userId);
        return buskingPort.getAllByBuskingIds(buskingIds);
    }
}

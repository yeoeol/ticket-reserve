package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCancelledEventPayload;
import ticket.reserve.core.event.payload.SubscriptionCreatedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.domain.BuskingSubscriptionCount;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.repository.BuskingSubscriptionCountRepository;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionUseCase {

    private final IdGenerator idGenerator;
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;
    private final BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void subscribeProcess(SubscriptionRequestDto request, String title, String location) {
        Optional<Subscription> optionalSubscription = subscriptionRepository
                .findByBuskingIdAndUserIdForUpdate(request.buskingId(), request.userId());

        Subscription subscription = null;
        // 같은 버스킹에 대해 구독 내역이 있다면 구독 상태만 변경
        if (optionalSubscription.isPresent()) {
            subscription = optionalSubscription.get();
            subscription.activate();
        } else {
            subscription = subscriptionRepository.save(request.toEntity(idGenerator));
        }

        int result = buskingSubscriptionCountRepository.increase(subscription.getBuskingId());
        if (result == 0) {
            buskingSubscriptionCountRepository.save(
                    BuskingSubscriptionCount.create(subscription.getBuskingId(), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.SUBSCRIPTION_CREATED,
                SubscriptionCreatedEventPayload.builder()
                        .subscriptionId(subscription.getId())
                        .userId(subscription.getUserId())
                        .buskingId(subscription.getBuskingId())
                        .title(title)
                        .location(location)
                        .startTime(subscription.getStartTime())
                        .buskingSubscriptionCount(count(subscription.getBuskingId()))
                        .build(),
                subscription.getBuskingId()
        );
    }

    @Transactional
    public void unsubscribeProcess(SubscriptionCancelRequestDto request) {
        Subscription subscription = subscriptionQueryService
                .getSubscription(request.buskingId(), request.userId());
        subscription.cancel();

        buskingSubscriptionCountRepository.decrease(subscription.getBuskingId());

        outboxEventPublisher.publish(
                EventType.SUBSCRIPTION_CANCELLED,
                SubscriptionCancelledEventPayload.builder()
                        .subscriptionId(subscription.getId())
                        .userId(subscription.getUserId())
                        .buskingId(subscription.getBuskingId())
                        .startTime(subscription.getStartTime())
                        .buskingSubscriptionCount(count(subscription.getBuskingId()))
                        .build(),
                subscription.getBuskingId()
        );
    }

    public Long count(Long buskingId) {
        return buskingSubscriptionCountRepository.findById(buskingId)
                .map(BuskingSubscriptionCount::getSubscriptionCount)
                .orElse(0L);
    }
}

package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.domain.Subscription;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final IdGenerator idGenerator;
    private final SubscriptionCrudService subscriptionCrudService;

    public void subscribe(SubscriptionRequestDto request) {
        subscriptionCrudService.save(request.toEntity(idGenerator));
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
}

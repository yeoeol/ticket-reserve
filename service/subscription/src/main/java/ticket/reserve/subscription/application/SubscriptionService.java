package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;

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
}

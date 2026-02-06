package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.port.out.RedisPort;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final IdGenerator idGenerator;
    private final RedisPort redisPort;
    private final SubscriptionCrudService subscriptionCrudService;

    public void subscribe(SubscriptionRequestDto request) {
        subscriptionCrudService.save(request.toEntity(idGenerator));
        redisPort.addToSubscriptionQueue(request.buskingId(), request.userId(), request.startTime());
    }

    public void unsubscribe(SubscriptionCancelRequestDto request) {
        redisPort.removeFromSubscriptionQueue(request.buskingId(), request.userId());
    }
}

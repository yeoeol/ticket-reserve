package ticket.reserve.subscription.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.port.out.RedisPort;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final RedisPort redisPort;

    public void subscription(SubscriptionRequestDto request) {
        redisPort.addToSubscriptionQueue(request.buskingId(), request.userId(), request.startTime());
    }
}

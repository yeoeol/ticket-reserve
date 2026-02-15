package ticket.reserve.hotbusking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.hotbusking.infrastructure.persistence.redis.BuskingSubscriptionCountRepository;

@Component
@RequiredArgsConstructor
public class HotBuskingScoreCalculator {

    private final BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;

    private static final long BUSKING_SUBSCRIPTION_COUNT_WEIGHT = 3;

    public long calculate(Long buskingId) {
        Long buskingSubscriptionCount = buskingSubscriptionCountRepository.read(buskingId);

        return buskingSubscriptionCount * BUSKING_SUBSCRIPTION_COUNT_WEIGHT;
    }
}

package ticket.reserve.hotbusking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ticket.reserve.hotbusking.application.port.out.BuskingSubscriptionCountPort;

@Component
@RequiredArgsConstructor
public class HotBuskingScoreCalculator {

    private final BuskingSubscriptionCountPort buskingSubscriptionCountPort;

    private static final long BUSKING_SUBSCRIPTION_COUNT_WEIGHT = 3;

    public long calculate(Long buskingId) {
        Long buskingSubscriptionCount = buskingSubscriptionCountPort.read(buskingId);

        return buskingSubscriptionCount * BUSKING_SUBSCRIPTION_COUNT_WEIGHT;
    }
}

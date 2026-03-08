package ticket.reserve.busking.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.busking.application.port.out.SubscriptionPort;

@Component
@RequiredArgsConstructor
public class SubscriptionRestClientAdapter implements SubscriptionPort {

    private final RestClient subscriptionRestClient;

    @Override
    public Boolean isSubscribe(Long buskingId, Long userId) {
        return subscriptionRestClient.get()
                .uri("/api/subscription?buskingId={buskingId}&userId={userId}", buskingId, userId)
                .retrieve()
                .body(Boolean.class);
    }
}

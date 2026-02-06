package ticket.reserve.busking.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.busking.application.dto.request.IsSubscribeRequestDto;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.application.port.out.SubscriptionPort;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;

@Component
public class SubscriptionRestClientAdapter implements SubscriptionPort {

    private final RestClient restClient;

    public SubscriptionRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${endpoints.ticket-reserve-subscription-service.url}") String subscriptionServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(subscriptionServiceUrl)
                .build();
    }

    @Override
    public Boolean isSubscribe(IsSubscribeRequestDto request) {
        return restClient.get()
                .uri("/api/subscription?buskingId={buskingId}&userId={userId}", request.buskingId(), request.userId())
                .retrieve()
                .body(Boolean.class);
    }
}

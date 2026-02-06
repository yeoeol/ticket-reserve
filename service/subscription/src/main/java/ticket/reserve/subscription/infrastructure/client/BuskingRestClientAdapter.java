package ticket.reserve.subscription.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;
import ticket.reserve.subscription.application.port.out.BuskingPort;

import java.util.List;

@Component
public class BuskingRestClientAdapter implements BuskingPort {

    private final RestClient restClient;

    public BuskingRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${endpoints.ticket-reserve-busking-service.url}") String buskingServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(buskingServiceUrl)
                .build();
    }

    @Override
    public List<BuskingResponseDto> getAllByBuskingIds(List<Long> buskingIds) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/buskings/bulk")
                        .queryParam("ids", buskingIds)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}

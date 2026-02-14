package ticket.reserve.hotbusking.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.hotbusking.application.dto.response.BuskingResponseDto;
import ticket.reserve.hotbusking.application.port.out.BuskingPort;

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
    public BuskingResponseDto get(Long buskingId) {
        return restClient.get()
                .uri("/api/buskings/{id}", buskingId)
                .retrieve()
                .body(BuskingResponseDto.class);
    }
}

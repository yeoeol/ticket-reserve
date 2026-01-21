package ticket.reserve.inventory.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.inventory.application.dto.response.BuskingResponseDto;
import ticket.reserve.inventory.application.port.out.BuskingPort;

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
    public BuskingResponseDto getOne(Long buskingId) {
        if (buskingId == null) {
            throw new CustomException(ErrorCode.BUSKING_NOT_FOUND);
        }

        return restClient.get()
                .uri("/api/buskings/{id}", buskingId)
                .retrieve()
                .body(BuskingResponseDto.class);
    }
}

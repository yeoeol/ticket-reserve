package ticket.reserve.hotbusking.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.hotbusking.application.dto.response.BuskingResponseDto;
import ticket.reserve.hotbusking.application.port.out.BuskingPort;

@Component
@RequiredArgsConstructor
public class BuskingRestClientAdapter implements BuskingPort {

    private final RestClient buskingRestClient;

    @Override
    public BuskingResponseDto get(Long buskingId) {
        return buskingRestClient.get()
                .uri("/api/buskings/{id}", buskingId)
                .retrieve()
                .body(BuskingResponseDto.class);
    }
}

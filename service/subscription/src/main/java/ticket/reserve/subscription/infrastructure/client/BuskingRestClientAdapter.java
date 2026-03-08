package ticket.reserve.subscription.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;
import ticket.reserve.subscription.application.port.out.BuskingPort;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BuskingRestClientAdapter implements BuskingPort {

    private final RestClient buskingRestClient;

    @Override
    public List<BuskingResponseDto> getAllByBuskingIds(List<Long> buskingIds) {
        if (buskingIds == null || buskingIds.isEmpty()) return Collections.emptyList();

        return buskingRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/buskings/bulk")
                        .queryParam("ids", buskingIds)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}

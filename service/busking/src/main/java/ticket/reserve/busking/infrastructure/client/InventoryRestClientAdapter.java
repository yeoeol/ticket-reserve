package ticket.reserve.busking.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;

@Component
public class InventoryRestClientAdapter implements InventoryPort {

    private final RestClient restClient;

    public InventoryRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${endpoints.ticket-reserve-inventory-service.url}") String inventoryServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(inventoryServiceUrl)
                .build();
    }

    @Override
    public Integer countsInventory(Long buskingId) {
        if (buskingId == null) {
            throw new CustomException(ErrorCode.EVENT_NOT_FOUND);
        }

        Integer count = restClient.get()
                .uri("/api/inventories/counts?id={id}", buskingId)
                .retrieve()
                .body(Integer.class);

        return count != null ? count : 0;
    }
}

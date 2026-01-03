package ticket.reserve.reservation.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.port.out.InventoryPort;

@Component
public class InventoryRestClientAdapter implements InventoryPort {

    private final RestClient restClient;

    public InventoryRestClientAdapter(
            @Value("${endpoints.ticket-reserve-inventory-service.url}") String inventoryServiceUrl
    ) {
        this.restClient = RestClient.create(inventoryServiceUrl);
    }

    @Override
    public void holdInventory(InventoryHoldRequestDto request) {
        restClient.post()
                .uri("/api/inventories/hold")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}

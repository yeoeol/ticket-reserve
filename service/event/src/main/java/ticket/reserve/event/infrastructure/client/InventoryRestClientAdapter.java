package ticket.reserve.event.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.event.application.port.out.InventoryPort;

@Component
@RequiredArgsConstructor
public class InventoryRestClientAdapter implements InventoryPort {

    // INVENTORY-SERVICE
    private final RestClient restClient = RestClient.create("http://localhost:30002");

    @Override
    public Integer countsInventory(Long eventId) {
        return restClient.get()
                .uri("/api/inventories/counts?eventId={eventId}", eventId)
                .retrieve()
                .body(Integer.class);
    }
}

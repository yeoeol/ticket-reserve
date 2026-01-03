package ticket.reserve.admin.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.CustomPageResponse;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;
import ticket.reserve.admin.application.port.out.InventoryPort;


@Component
public class InventoryRestClientAdapter implements InventoryPort {

    private final RestClient restClient;

    public InventoryRestClientAdapter(
            @Value("${endpoints.ticket-reserve-inventory-service.url}") String inventoryServiceUrl
    ) {
        this.restClient = RestClient.create(inventoryServiceUrl);
    }


    @Override
    public CustomPageResponse<InventoryResponseDto> getInventories(Long eventId, int page) {
        return restClient.get()
                .uri("/api/inventories/{eventId}?page={page}", eventId, page)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public InventoryResponseDto getInventory(Long eventId, Long inventoryId) {
        return restClient.get()
                .uri("/api/inventories/{eventId}/{inventoryId}", eventId, inventoryId)
                .retrieve()
                .body(InventoryResponseDto.class);
    }

    @Override
    public void createInventory(InventoryRequestDto request) {
        restClient.post()
                .uri("/api/inventories")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateInventory(Long eventId, Long inventoryId, InventoryUpdateRequestDto request) {
        restClient.put()
                .uri("/api/inventories/{eventId}/{inventoryId}", eventId, inventoryId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteInventory(Long eventId, Long inventoryId) {
        restClient.delete()
                .uri("/api/inventories/{eventId}/{inventoryId}", eventId, inventoryId)
                .retrieve()
                .toBodilessEntity();
    }
}

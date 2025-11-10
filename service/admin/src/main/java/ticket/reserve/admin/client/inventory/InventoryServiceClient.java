package ticket.reserve.admin.client.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.admin.client.inventory.dto.InventoryDetailResponseDto;
import ticket.reserve.admin.client.inventory.dto.InventoryListResponseDto;
import ticket.reserve.admin.client.inventory.dto.InventoryRequestDto;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/{eventId}")
    InventoryListResponseDto getInventories(@PathVariable Long eventId);

    @GetMapping("/api/inventory/{eventId}/{inventoryId}")
    InventoryDetailResponseDto getInventory(@PathVariable("eventId") Long eventId,
                                            @PathVariable("inventoryId") Long inventoryId);

    @PostMapping("/api/inventory")
    void createInventory(@RequestBody InventoryRequestDto request);
}

package ticket.reserve.admin.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.inventory.response.CustomPageResponse;
import ticket.reserve.admin.application.port.out.InventoryPort;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient extends InventoryPort {

    @GetMapping("/api/inventories/{eventId}")
    CustomPageResponse<InventoryResponseDto> getInventories(
            @PathVariable Long eventId,
            @RequestParam(value = "page", defaultValue = "0") int page
    );

    @GetMapping("/api/inventories/{eventId}/{inventoryId}")
    InventoryResponseDto getInventory(@PathVariable("eventId") Long eventId,
                                      @PathVariable("inventoryId") Long inventoryId);

    @PostMapping("/api/inventories")
    void createInventory(@RequestBody InventoryRequestDto request);

    @PutMapping("/api/inventories/{eventId}/{inventoryId}")
    void updateInventory(@PathVariable("eventId") Long eventId,
                         @PathVariable("inventoryId") Long inventoryId,
                         @RequestBody InventoryUpdateRequestDto request);

    @DeleteMapping("/api/inventories/{eventId}/{inventoryId}")
    void deleteInventory(@PathVariable("eventId") Long eventId,
                         @PathVariable("inventoryId") Long inventoryId);
}

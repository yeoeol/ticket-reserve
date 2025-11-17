package ticket.reserve.admin.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.port.out.InventoryPort;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient extends InventoryPort {

    @GetMapping("/api/inventory/{eventId}")
    List<InventoryResponseDto> getInventories(@PathVariable Long eventId);

    @GetMapping("/api/inventory/{eventId}/{inventoryId}")
    InventoryResponseDto getInventory(@PathVariable("eventId") Long eventId,
                                      @PathVariable("inventoryId") Long inventoryId);

    @PostMapping("/api/inventory")
    void createInventory(@RequestBody InventoryRequestDto request);

    @PutMapping("/api/inventory/{eventId}/{inventoryId}")
    void updateInventory(@PathVariable("eventId") Long eventId,
                         @PathVariable("inventoryId") Long inventoryId,
                         @RequestBody InventoryUpdateRequestDto request);

    @DeleteMapping("/api/inventory/{eventId}/{inventoryId}")
    void deleteInventory(@PathVariable("eventId") Long eventId,
                         @PathVariable("inventoryId") Long inventoryId);
}

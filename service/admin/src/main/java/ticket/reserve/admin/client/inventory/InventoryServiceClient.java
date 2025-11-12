package ticket.reserve.admin.client.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.client.inventory.dto.*;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {

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

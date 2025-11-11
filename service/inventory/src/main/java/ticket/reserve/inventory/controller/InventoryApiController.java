package ticket.reserve.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.dto.*;
import ticket.reserve.inventory.service.InventoryService;

@RestController
@RequiredArgsConstructor
public class InventoryApiController {

    private final InventoryService inventoryService;

    @PostMapping("/api/inventory")
    public ResponseEntity<Void> createInventory(@RequestBody InventoryRequestDto request) {
        inventoryService.createInventory(request);
        return ResponseEntity.ok().build();
    }

    // 좌석 선점 로직
    @PostMapping("/api/inventory/hold")
    public ResponseEntity<Void> holdInventory(@RequestBody InventoryHoldRequestDto request) {
        inventoryService.holdInventory(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/inventory/confirm")
    public ResponseEntity<Void> confirmInventory(@RequestBody InventoryConfirmRequestDto request) {
        inventoryService.confirmInventory(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/inventory/release")
    public ResponseEntity<Void> releaseInventory(@RequestBody InventoryReleaseRequestDto request) {
        inventoryService.releaseInventory(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/inventory/counts")
    public ResponseEntity<Integer> countsInventory(@RequestParam("eventId") Long eventId) {
        return ResponseEntity.ok(inventoryService.getAvailableInventoryCounts(eventId));
    }

    @GetMapping("/api/inventory/{eventId}")
    public ResponseEntity<InventoryListResponseDto> getInventories(@PathVariable Long eventId) {
        return ResponseEntity.ok(inventoryService.getInventories(eventId));
    }

    @GetMapping("/api/inventory/{eventId}/{inventoryId}")
    public ResponseEntity<InventoryDetailResponseDto> getInventory(@PathVariable("eventId") Long eventId,
                                                                   @PathVariable("inventoryId") Long inventoryId) {
        return ResponseEntity.ok(inventoryService.getInventory(eventId, inventoryId));
    }

    @PutMapping("/api/inventory/{eventId}/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable("eventId") Long eventId,
                                                @PathVariable("inventoryId") Long inventoryId,
                                                @RequestBody InventoryUpdateRequestDto request) {
        inventoryService.updateInventory(inventoryId, request);
        return ResponseEntity.ok().build();
    }
}

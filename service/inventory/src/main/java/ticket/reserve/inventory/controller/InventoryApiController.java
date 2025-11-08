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

    @PostMapping("/admin/api/inventory")
    public ResponseEntity<InventoryCreateResponseDto> create(@RequestBody InventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
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
    public ResponseEntity<Integer> countsInventory(@RequestBody InventoryCountsRequestDto request) {
        return ResponseEntity.ok(inventoryService.getAvailableInventoryCounts(request.eventId()));
    }
}

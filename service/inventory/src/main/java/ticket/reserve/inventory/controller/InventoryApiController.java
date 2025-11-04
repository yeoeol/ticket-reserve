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

    @PostMapping("/api/inventory/{eventId}/reserve")
    public ResponseEntity<InventoryResponseDto> reserve(
            @PathVariable Long eventId,
            @RequestParam Long inventoryId
    ) {
        return ResponseEntity.ok(inventoryService.reserveSeats(eventId, inventoryId));
    }

    @PostMapping("/api/inventory/{eventId}/release")
    public ResponseEntity<Void> release(
            @PathVariable Long eventId,
            @RequestParam Long inventoryId
    ) {
        inventoryService.releaseSeats(eventId, inventoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/api/inventory")
    public ResponseEntity<InventoryCreateResponseDto> create(@RequestBody InventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

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
}

package ticket.reserve.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.dto.InventoryRequestDto;
import ticket.reserve.inventory.dto.InventoryResponseDto;
import ticket.reserve.inventory.service.InventoryService;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/admin/inventory")
    public ResponseEntity<InventoryResponseDto> create(@RequestBody InventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @GetMapping("/inventory/{eventId}")
    public ResponseEntity<InventoryResponseDto> getOne(@PathVariable Long eventId) {
        return ResponseEntity.ok(inventoryService.getInventory(eventId));
    }

    @PostMapping("/inventory/{eventId}/reserve")
    public ResponseEntity<InventoryResponseDto> reserve(
            @PathVariable Long eventId,
            @RequestParam int count
    ) {
        return ResponseEntity.ok(inventoryService.reserveSeats(eventId, count));
    }

    @PostMapping("/inventory/{eventId}/release")
    public ResponseEntity<Void> release(
            @PathVariable Long eventId,
            @RequestParam int count
    ) {
        inventoryService.releaseSeats(eventId, count);
        return ResponseEntity.noContent().build();
    }
}

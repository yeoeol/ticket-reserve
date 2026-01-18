package ticket.reserve.inventory.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.application.InventoryService;
import ticket.reserve.inventory.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.inventory.application.dto.request.InventoryRequestDto;
import ticket.reserve.inventory.application.dto.request.InventoryUpdateRequestDto;
import ticket.reserve.inventory.application.dto.response.CustomPageResponse;
import ticket.reserve.inventory.application.dto.response.InventoryResponseDto;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryApiController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<Void> createInventory(@Valid @RequestBody InventoryRequestDto request) {
        inventoryService.createInventory(request);
        return ResponseEntity.ok().build();
    }

    // 좌석 선점 로직
    @PostMapping("/hold")
    public ResponseEntity<Void> holdInventory(@Valid @RequestBody InventoryHoldRequestDto request) {
        inventoryService.holdInventory(request.buskingId(), request.inventoryId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/counts")
    public ResponseEntity<Integer> countInventory(@RequestParam("id") Long buskingId) {
        return ResponseEntity.ok(inventoryService.getAvailableInventoryCounts(buskingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomPageResponse<InventoryResponseDto>> getInventories(
            @PathVariable("id") Long buskingId,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(inventoryService.getInventoryPaging(buskingId, pageable));
    }

    @GetMapping("/{id}/{inventoryId}")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable("id") Long buskingId,
                                                             @PathVariable("inventoryId") Long inventoryId) {
        return ResponseEntity.ok(inventoryService.getInventory(buskingId, inventoryId));
    }

    @PutMapping("/{id}/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable("id") Long buskingId,
                                                @PathVariable("inventoryId") Long inventoryId,
                                                @RequestBody InventoryUpdateRequestDto request) {
        inventoryService.updateInventory(buskingId, inventoryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("id") Long buskingId,
                                                @PathVariable("inventoryId") Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.ok().build();
    }
}

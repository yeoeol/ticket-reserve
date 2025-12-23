package ticket.reserve.inventory.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.application.InventoryService;
import ticket.reserve.inventory.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.inventory.application.dto.request.InventoryReleaseRequestDto;
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
    public ResponseEntity<Void> createInventory(@RequestBody InventoryRequestDto request) {
        inventoryService.createInventory(request);
        return ResponseEntity.ok().build();
    }

    // 좌석 선점 로직
    @PostMapping("/hold")
    public ResponseEntity<Void> holdInventory(@RequestBody InventoryHoldRequestDto request) {
        inventoryService.holdInventory(request.inventoryId());
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/release")
//    public ResponseEntity<Void> releaseInventory(@RequestBody InventoryReleaseRequestDto request) {
//        inventoryService.releaseInventory(request.inventoryId());
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/counts")
    public ResponseEntity<Integer> countsInventory(@RequestParam("eventId") Long eventId) {
        return ResponseEntity.ok(inventoryService.getAvailableInventoryCounts(eventId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<CustomPageResponse<InventoryResponseDto>> getInventories(
            @PathVariable Long eventId,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(inventoryService.getInventoryPaging(eventId, pageable));
    }

    @GetMapping("/{eventId}/{inventoryId}")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable("eventId") Long eventId,
                                                             @PathVariable("inventoryId") Long inventoryId) {
        return ResponseEntity.ok(inventoryService.getInventory(inventoryId));
    }

    @PutMapping("/{eventId}/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable("eventId") Long eventId,
                                                @PathVariable("inventoryId") Long inventoryId,
                                                @RequestBody InventoryUpdateRequestDto request) {
        inventoryService.updateInventory(inventoryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("eventId") Long eventId,
                                                @PathVariable("inventoryId") Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.ok().build();
    }
}

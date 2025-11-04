package ticket.reserve.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.dto.InventoryCreateResponseDto;
import ticket.reserve.inventory.dto.InventoryListResponseDto;
import ticket.reserve.inventory.dto.InventoryRequestDto;
import ticket.reserve.inventory.dto.InventoryResponseDto;
import ticket.reserve.inventory.service.InventoryService;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/admin/inventory")
    public ResponseEntity<InventoryCreateResponseDto> create(@RequestBody InventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @GetMapping("/inventory/{eventId}")
    public String getAll(@PathVariable Long eventId, Model model) {
        InventoryListResponseDto inventoryList = inventoryService.getInventoryList(eventId);
        model.addAttribute("inventoryList", inventoryList);

        return "inventory-list";
    }

    @PostMapping("/inventory/{eventId}/reserve")
    public String reserve(
            @PathVariable Long eventId,
            @RequestParam Long inventoryId,
            Model model
    ) {
        InventoryResponseDto inventory = inventoryService.reserveSeats(eventId, inventoryId);
        model.addAttribute("inventory", inventory);

        return "redirect:/payments";
    }

    @PostMapping("/inventory/{eventId}/release")
    public String release(
            @PathVariable Long eventId,
            @RequestParam Long inventoryId
    ) {
        inventoryService.releaseSeats(eventId, inventoryId);

        return "redirect:/inventory/" + eventId;
    }
}

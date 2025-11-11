package ticket.reserve.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.client.event.dto.EventRequestDto;
import ticket.reserve.admin.client.event.dto.EventUpdateRequestDto;
import ticket.reserve.admin.client.inventory.dto.InventoryDetailResponseDto;
import ticket.reserve.admin.client.inventory.dto.InventoryListResponseDto;
import ticket.reserve.admin.client.inventory.dto.InventoryRequestDto;
import ticket.reserve.admin.client.inventory.dto.InventoryUpdateRequestDto;
import ticket.reserve.admin.service.AdminService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminInventoryController {

    private final AdminService adminService;

    @GetMapping("/admin/{eventId}/inventory")
    public String getInventories(@PathVariable("eventId") Long eventId, Model model) {
        InventoryListResponseDto inventories = adminService.getInventories(eventId);

        model.addAttribute("inventories", inventories);
        return "admin/inventory";
    }

    @GetMapping("/admin/{eventId}/inventory/{inventoryId}")
    public String getInventory(@PathVariable("eventId") Long eventId,
                               @PathVariable("inventoryId") Long inventoryId,
                               Model model) {
        InventoryDetailResponseDto inventory = adminService.getInventory(eventId, inventoryId);

        model.addAttribute("eventId", eventId);
        model.addAttribute("inventory", inventory);
        return "admin/inventorydetails";
    }

    @GetMapping("/admin/{eventId}/inventory/create")
    public String createInventoryPage(@PathVariable Long eventId, Model model) {
        model.addAttribute("inventory", new InventoryDetailResponseDto());
        return "admin/inventorydetails";
    }

    @PostMapping("/admin/{eventId}/inventory")
    public String createInventory(@PathVariable Long eventId,
                                  @ModelAttribute InventoryRequestDto request) {
        adminService.createInventory(request);
        return "redirect:/admin/%d/inventory".formatted(eventId);
    }

    @PutMapping("/admin/{eventId}/inventory/{inventoryId}")
    public String updateInventory(@PathVariable("eventId") Long eventId,
                                  @PathVariable("inventoryId") Long inventoryId,
                                  @ModelAttribute InventoryUpdateRequestDto request) {
        adminService.updateInventory(eventId, inventoryId, request);
        return "redirect:/admin/%d/inventory".formatted(eventId);
    }
}

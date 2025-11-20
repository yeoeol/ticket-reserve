package ticket.reserve.admin.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryListPageDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;
import ticket.reserve.admin.application.AdminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/inventories")
public class AdminInventoryController {

    private final AdminService adminService;

    @GetMapping
    public String getInventoryListPage(
            @PathVariable("eventId") Long eventId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        InventoryListPageDto inventoryListPageDto = adminService.getInventoryListPageData(eventId, page);

        model.addAttribute("event", inventoryListPageDto.event());
        model.addAttribute("inventoryList", inventoryListPageDto.inventoryList().getContent());
        model.addAttribute("inventoryPage", inventoryListPageDto.inventoryList());
        return "admin/inventory";
    }

    @GetMapping("/{inventoryId}")
    public String getInventory(@PathVariable("eventId") Long eventId,
                               @PathVariable("inventoryId") Long inventoryId,
                               Model model
    ) {
        InventoryResponseDto inventory = adminService.getInventory(eventId, inventoryId);

        model.addAttribute("eventId", eventId);
        model.addAttribute("inventory", inventory);
        return "admin/inventorydetails";
    }

    @GetMapping("/new")
    public String createInventoryPage(@PathVariable Long eventId, Model model) {
        model.addAttribute("inventory", new InventoryResponseDto(eventId));
        return "admin/inventorydetails";
    }

    @PostMapping
    public String createInventory(@PathVariable Long eventId,
                                  @ModelAttribute InventoryRequestDto request) {
        adminService.createInventory(request);
        return "redirect:/admin/%d/inventory".formatted(eventId);
    }

    @PutMapping("/{inventoryId}")
    public String updateInventory(@PathVariable("eventId") Long eventId,
                                  @PathVariable("inventoryId") Long inventoryId,
                                  @ModelAttribute InventoryUpdateRequestDto request
    ) {
        adminService.updateInventory(eventId, inventoryId, request);
        return "redirect:/admin/%d/inventory".formatted(eventId);
    }

    @DeleteMapping("/{inventoryId}")
    public String deleteInventory(@PathVariable("eventId") Long eventId,
                                  @PathVariable("inventoryId") Long inventoryId
    ) {
        adminService.deleteInventory(eventId, inventoryId);
        return "redirect:/admin/%d/inventory".formatted(eventId);
    }
}

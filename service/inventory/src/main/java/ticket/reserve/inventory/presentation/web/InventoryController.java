package ticket.reserve.inventory.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.inventory.application.dto.response.InventoryListResponseDto;
import ticket.reserve.inventory.application.InventoryService;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventories/{eventId}")
    public String getAll(@PathVariable Long eventId, Model model) {
        InventoryListResponseDto inventoryList = inventoryService.getInventories(eventId);
        model.addAttribute("inventoryList", inventoryList);

        return "inventory-list";
    }
}

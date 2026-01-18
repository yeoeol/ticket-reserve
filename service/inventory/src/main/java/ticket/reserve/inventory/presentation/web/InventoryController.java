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

    @GetMapping("/inventories/{id}")
    public String getAll(@PathVariable("id") Long buskingId, Model model) {
        InventoryListResponseDto inventoryList = inventoryService.getInventories(buskingId);
        model.addAttribute("inventoryList", inventoryList);

        return "inventory-list";
    }
}

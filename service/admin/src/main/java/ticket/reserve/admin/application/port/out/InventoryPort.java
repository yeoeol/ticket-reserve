package ticket.reserve.admin.application.port.out;

import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.CustomPageResponse;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;

public interface InventoryPort {
    InventoryResponseDto getInventory(Long eventId, Long inventoryId);

    void createInventory(InventoryRequestDto request);

    void updateInventory(Long eventId, Long inventoryId, InventoryUpdateRequestDto request);

    void deleteInventory(Long eventId, Long inventoryId);

    CustomPageResponse<InventoryResponseDto> getInventories(Long eventId, int page);
}

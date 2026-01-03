package ticket.reserve.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryListPageDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;
import ticket.reserve.admin.application.port.out.InventoryPort;

@Service
@RequiredArgsConstructor
public class AdminInventoryService {

    private final AdminEventService adminEventService;
    private final InventoryPort inventoryPort;

    public InventoryResponseDto getInventory(Long eventId, Long inventoryId) {
        return inventoryPort.getInventory(eventId, inventoryId);
    }

    public void createInventory(InventoryRequestDto request) {
        inventoryPort.createInventory(request);
    }

    public void updateInventory(Long eventId, Long inventoryId, InventoryUpdateRequestDto request) {
        inventoryPort.updateInventory(eventId, inventoryId, request);
    }

    public void deleteInventory(Long eventId, Long inventoryId) {
        inventoryPort.deleteInventory(eventId, inventoryId);
    }

    public InventoryListPageDto getInventoryListPageData(Long eventId, int page) {
        return InventoryListPageDto.of(
                adminEventService.getEvent(eventId),
                inventoryPort.getInventories(eventId, page)
        );
    }
}

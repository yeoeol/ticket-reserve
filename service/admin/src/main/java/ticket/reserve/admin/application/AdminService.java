package ticket.reserve.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.application.port.out.EventPort;
import ticket.reserve.admin.application.port.out.InventoryPort;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.dto.event.response.EventResponseDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryListPageDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EventPort eventPort;
    private final InventoryPort inventoryPort;

    /**
     * EVENT-SERVICE
     */
    public List<EventResponseDto> getEvents() {
        return eventPort.getEvents();
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        return eventPort.getEvent(eventId);
    }

    public EventDetailResponseDto createEvent(EventRequestDto request) {
        return eventPort.createEvent(request);
    }

    public void updateEvent(Long eventId, EventUpdateRequestDto request) {
        eventPort.updateEvent(eventId, request);
    }

    public void deleteEvent(Long eventId) {
        eventPort.deleteEvent(eventId);
    }

    /**
     * INVENTORY-SERVICE
     */
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
                eventPort.getEvent(eventId),
                inventoryPort.getInventories(eventId, page)
        );
    }
}

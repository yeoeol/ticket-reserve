package ticket.reserve.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.application.port.out.EventPort;
import ticket.reserve.admin.application.port.out.InventoryPort;
import ticket.reserve.admin.application.port.out.UserPort;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.dto.event.response.EventResponseDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryRequestDto;
import ticket.reserve.admin.application.dto.inventory.request.InventoryUpdateRequestDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryResponseDto;
import ticket.reserve.admin.application.dto.inventory.response.InventoryListPageDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserPort userPort;
    private final EventPort eventPort;
    private final InventoryPort inventoryPort;

    /**
     * USER-SERVICE
     */
    public void logout(String accessToken) {
        Map<String, String> request = new HashMap<>();
        request.put("accessToken", accessToken);
        userPort.logout(request);
    }

    public List<UserResponseDto> getUsers() {
        return userPort.getUsers();
    }

    public UserResponseDto getUser(Long userId) {
        return userPort.getUser(userId);
    }

    public void updateUser(UserUpdateRequestDto request) {
        userPort.updateUser(request);
    }

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

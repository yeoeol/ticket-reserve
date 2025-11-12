package ticket.reserve.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.client.event.EventServiceClient;
import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;
import ticket.reserve.admin.client.event.dto.EventRequestDto;
import ticket.reserve.admin.client.event.dto.EventResponseDto;
import ticket.reserve.admin.client.event.dto.EventUpdateRequestDto;
import ticket.reserve.admin.client.inventory.InventoryServiceClient;
import ticket.reserve.admin.client.inventory.dto.*;
import ticket.reserve.admin.client.user.UserServiceClient;
import ticket.reserve.admin.client.user.dto.UserResponseDto;
import ticket.reserve.admin.client.user.dto.UserUpdateRequestDto;
import ticket.reserve.admin.dto.InventoryListPageDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    /**
     * USER-SERVICE
     */
    public List<UserResponseDto> getUsers() {
        return userServiceClient.getUsers();
    }

    public UserResponseDto getUser(Long userId) {
        return userServiceClient.getUser(userId);
    }

    public void updateUser(UserUpdateRequestDto request) {
        userServiceClient.updateUser(request);
    }

    /**
     * EVENT-SERVICE
     */
    public List<EventResponseDto> getEvents() {
        return eventServiceClient.getEvents();
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        return eventServiceClient.getEvent(eventId);
    }

    public EventDetailResponseDto createEvent(EventRequestDto request) {
        return eventServiceClient.createEvent(request);
    }

    public void updateEvent(Long eventId, EventUpdateRequestDto request) {
        eventServiceClient.updateEvent(eventId, request);
    }

    public void deleteEvent(Long eventId) {
        eventServiceClient.deleteEvent(eventId);
    }

    /**
     * INVENTORY-SERVICE
     */
    public InventoryResponseDto getInventory(Long eventId, Long inventoryId) {
        return inventoryServiceClient.getInventory(eventId, inventoryId);
    }

    public void createInventory(InventoryRequestDto request) {
        inventoryServiceClient.createInventory(request);
    }

    public void updateInventory(Long eventId, Long inventoryId, InventoryUpdateRequestDto request) {
        inventoryServiceClient.updateInventory(eventId, inventoryId, request);
    }

    public void deleteInventory(Long eventId, Long inventoryId) {
        inventoryServiceClient.deleteInventory(eventId, inventoryId);
    }

    public InventoryListPageDto getInventoryListPageData(Long eventId) {
        return InventoryListPageDto.of(
                eventServiceClient.getEvent(eventId),
                inventoryServiceClient.getInventories(eventId)
        );
    }
}

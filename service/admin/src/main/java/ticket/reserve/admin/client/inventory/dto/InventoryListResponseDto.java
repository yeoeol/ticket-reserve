package ticket.reserve.admin.client.inventory.dto;

import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;

import java.util.List;

public record InventoryListResponseDto(
        EventDetailResponseDto event,
        List<InventoryResponseDto> inventoryList
) {
}
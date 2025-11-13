package ticket.reserve.admin.dto;

import lombok.Builder;
import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;
import ticket.reserve.admin.client.inventory.dto.InventoryResponseDto;

import java.util.List;

@Builder
public record InventoryListPageDto(
        EventDetailResponseDto event,
        List<InventoryResponseDto> inventoryList
) {
    public static InventoryListPageDto of(EventDetailResponseDto event, List<InventoryResponseDto> inventoryList) {
        return InventoryListPageDto.builder()
                .event(event)
                .inventoryList(inventoryList)
                .build();
    }
}

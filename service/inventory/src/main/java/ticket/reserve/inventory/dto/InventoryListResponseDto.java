package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.client.dto.EventDetailResponseDto;

import java.util.List;

@Builder
public record InventoryListResponseDto(
        EventDetailResponseDto event,
        List<InventoryResponseDto> inventoryList
) {
    public static InventoryListResponseDto of(EventDetailResponseDto event, List<InventoryResponseDto> inventoryList) {
        return InventoryListResponseDto.builder()
                .event(event)
                .inventoryList(inventoryList)
                .build();
    }
}

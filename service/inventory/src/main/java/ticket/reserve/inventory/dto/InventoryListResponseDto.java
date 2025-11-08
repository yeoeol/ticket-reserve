package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.client.dto.EventResponseDto;

import java.util.List;

@Builder
public record InventoryListResponseDto(
        EventResponseDto event,
        List<InventoryResponseDto> inventoryList
) {
    public static InventoryListResponseDto of(EventResponseDto event, List<InventoryResponseDto> inventoryList) {
        return InventoryListResponseDto.builder()
                .event(event)
                .inventoryList(inventoryList)
                .build();
    }
}

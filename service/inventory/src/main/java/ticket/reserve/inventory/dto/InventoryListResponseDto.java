package ticket.reserve.inventory.dto;

import lombok.Builder;

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

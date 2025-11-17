package ticket.reserve.inventory.application.dto.response;

import lombok.Builder;

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

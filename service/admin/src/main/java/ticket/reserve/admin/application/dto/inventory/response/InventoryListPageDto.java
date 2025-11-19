package ticket.reserve.admin.application.dto.inventory.response;

import lombok.Builder;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;

@Builder
public record InventoryListPageDto(
        EventDetailResponseDto event,
        CustomPageResponse<InventoryResponseDto> inventoryList
) {
    public static InventoryListPageDto of(EventDetailResponseDto event, CustomPageResponse<InventoryResponseDto> inventoryList) {
        return InventoryListPageDto.builder()
                .event(event)
                .inventoryList(inventoryList)
                .build();
    }
}

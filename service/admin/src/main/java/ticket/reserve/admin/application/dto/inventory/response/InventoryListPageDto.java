package ticket.reserve.admin.application.dto.inventory.response;

import lombok.Builder;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;

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

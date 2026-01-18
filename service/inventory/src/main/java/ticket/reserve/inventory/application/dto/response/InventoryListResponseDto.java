package ticket.reserve.inventory.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InventoryListResponseDto(
        BuskingResponseDto busking,
        List<InventoryResponseDto> inventoryList
) {
    public static InventoryListResponseDto of(BuskingResponseDto busking, List<InventoryResponseDto> inventoryList) {
        return InventoryListResponseDto.builder()
                .busking(busking)
                .inventoryList(inventoryList)
                .build();
    }
}

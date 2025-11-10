package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;

@Builder
public record InventoryDetailResponseDto(
        Long inventoryId,
        String eventTitle,
        int price,
        String status
) {
    public static InventoryDetailResponseDto of(String eventTitle, Inventory inventory) {
        return InventoryDetailResponseDto.builder()
                .inventoryId(inventory.getId())
                .eventTitle(eventTitle)
                .price(inventory.getPrice())
                .status(inventory.getStatus().name())
                .build();
    }
}

package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long eventId,
        String inventoryName,
        int price,
        String status
) {
    public static InventoryResponseDto from(Inventory inventory) {
        return InventoryResponseDto.builder()
                .inventoryId(inventory.getId())
                .eventId(inventory.getEventId())
                .inventoryName(inventory.getInventoryName())
                .price(inventory.getPrice())
                .status(inventory.getStatus().name())
                .build();
    }
}

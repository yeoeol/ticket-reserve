package ticket.reserve.inventory.application.dto.response;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;

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

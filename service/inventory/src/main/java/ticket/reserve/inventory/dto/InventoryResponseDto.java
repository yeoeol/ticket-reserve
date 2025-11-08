package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long eventId,
        int price,
        InventoryStatus status,
        int totalSeats,
        int availableSeats
) {
    public static InventoryResponseDto from(Inventory inventory) {
        return InventoryResponseDto.builder()
                .inventoryId(inventory.getId())
                .eventId(inventory.getEventId())
                .price(inventory.getPrice())
                .status(inventory.getStatus())
                .build();
    }
}

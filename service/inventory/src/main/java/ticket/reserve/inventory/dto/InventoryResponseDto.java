package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long eventId,
        int totalSeats,
        int availableSeats
) {
    public static InventoryResponseDto from(Inventory inventory) {
        return InventoryResponseDto.builder()
                .inventoryId(inventory.getId())
                .eventId(inventory.getEventId())
                .totalSeats(inventory.getTotalSeats())
                .availableSeats(inventory.getAvailableSeats())
                .build();
    }
}

package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;

@Builder
public record InventoryResponseDto(
        Long eventId,
        int totalSeats,
        int availableSeats
) {
    public static InventoryResponseDto from(Inventory inventory) {
        return InventoryResponseDto.builder()
                .eventId(inventory.getEventId())
                .totalSeats(inventory.getTotalSeats())
                .availableSeats(inventory.getAvailableSeats())
                .build();
    }
}

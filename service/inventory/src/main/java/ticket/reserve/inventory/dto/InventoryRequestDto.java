package ticket.reserve.inventory.dto;

import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;

public record InventoryRequestDto(
        Long eventId,
        int price,
        int totalSeats
) {
    public Inventory toEntity() {
        return Inventory.builder()
                .eventId(this.eventId)
                .price(this.price)
                .status(InventoryStatus.AVAILABLE)
                .totalSeats(this.totalSeats)
                .availableSeats(this.totalSeats)
                .build();
    }
}

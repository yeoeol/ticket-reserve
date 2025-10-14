package ticket.reserve.inventory.dto;

import ticket.reserve.inventory.domain.Inventory;

public record InventoryRequestDto(
        Long eventId,
        int totalSeats
) {
    public Inventory toEntity() {
        return Inventory.builder()
                .eventId(this.eventId)
                .totalSeats(this.totalSeats)
                .availableSeats(this.totalSeats)
                .build();
    }
}

package ticket.reserve.inventory.dto;

import ticket.reserve.inventory.domain.Inventory;

public record InventoryRequestDto(
        Long eventId,
        int price
) {
    public Inventory toEntity() {
        return Inventory.builder()
                .eventId(this.eventId)
                .price(this.price)
                .build();
    }
}

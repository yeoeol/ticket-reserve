package ticket.reserve.inventory.dto;

import ticket.reserve.inventory.domain.Inventory;

public record InventoryRequestDto(
        String inventoryName,
        Long eventId,
        int price
) {
    public Inventory toEntity() {
        return Inventory.builder()
                .inventoryName(this.inventoryName)
                .eventId(this.eventId)
                .price(this.price)
                .build();
    }
}

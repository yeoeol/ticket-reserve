package ticket.reserve.inventory.application.dto.request;

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

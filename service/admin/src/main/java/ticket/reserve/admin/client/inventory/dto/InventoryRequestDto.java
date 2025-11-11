package ticket.reserve.admin.client.inventory.dto;

public record InventoryRequestDto(
        String inventoryName,
        Long eventId,
        int price
) {
}

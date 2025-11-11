package ticket.reserve.admin.client.inventory.dto;

public record InventoryUpdateRequestDto(
        String inventoryName,
        int price
) {
}

package ticket.reserve.inventory.dto;

public record InventoryUpdateRequestDto(
        String inventoryName,
        int price
) {
}

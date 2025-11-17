package ticket.reserve.inventory.application.dto.request;

public record InventoryUpdateRequestDto(
        String inventoryName,
        int price
) {
}

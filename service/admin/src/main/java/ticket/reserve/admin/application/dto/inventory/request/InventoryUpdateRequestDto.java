package ticket.reserve.admin.application.dto.inventory.request;

public record InventoryUpdateRequestDto(
        String inventoryName,
        int price
) {
}

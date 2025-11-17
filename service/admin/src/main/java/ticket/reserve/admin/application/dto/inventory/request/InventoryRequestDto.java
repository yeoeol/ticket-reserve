package ticket.reserve.admin.application.dto.inventory.request;

public record InventoryRequestDto(
        String inventoryName,
        Long eventId,
        int price
) {
}

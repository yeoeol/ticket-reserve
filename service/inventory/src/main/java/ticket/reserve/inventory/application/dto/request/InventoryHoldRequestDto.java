package ticket.reserve.inventory.application.dto.request;

public record InventoryHoldRequestDto(
        Long eventId,
        Long inventoryId
) {
}

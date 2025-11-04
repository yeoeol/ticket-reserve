package ticket.reserve.inventory.dto;

public record InventoryHoldRequestDto(
        Long eventId,
        Long inventoryId
) {
}

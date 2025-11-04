package ticket.reserve.inventory.dto;

public record InventoryConfirmRequestDto(
        Long eventId,
        Long inventoryId
) {
}

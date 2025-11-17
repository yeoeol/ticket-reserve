package ticket.reserve.inventory.application.dto.request;

public record InventoryConfirmRequestDto(
        Long eventId,
        Long inventoryId
) {
}

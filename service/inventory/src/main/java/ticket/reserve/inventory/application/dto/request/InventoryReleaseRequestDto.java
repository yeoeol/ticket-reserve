package ticket.reserve.inventory.application.dto.request;

public record InventoryReleaseRequestDto(
        Long eventId,
        Long inventoryId
) {
}

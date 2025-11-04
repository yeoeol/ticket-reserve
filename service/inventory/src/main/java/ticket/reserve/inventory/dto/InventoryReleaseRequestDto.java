package ticket.reserve.inventory.dto;

public record InventoryReleaseRequestDto(
        Long eventId,
        Long inventoryId
) {
}

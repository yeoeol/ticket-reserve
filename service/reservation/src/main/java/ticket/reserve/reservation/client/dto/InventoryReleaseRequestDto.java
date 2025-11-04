package ticket.reserve.reservation.client.dto;

public record InventoryReleaseRequestDto(
        Long eventId,
        Long inventoryId
) {
}

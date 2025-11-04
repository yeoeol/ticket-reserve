package ticket.reserve.reservation.dto;

public record InventoryReleaseRequestDto(
        Long eventId,
        Long inventoryId
) {
}

package ticket.reserve.reservation.dto;

public record InventoryHoldRequestDto(
        Long eventId,
        Long inventoryId
) {
}

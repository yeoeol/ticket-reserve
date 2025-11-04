package ticket.reserve.reservation.client.dto;

public record InventoryHoldRequestDto(
        Long eventId,
        Long inventoryId
) {
}

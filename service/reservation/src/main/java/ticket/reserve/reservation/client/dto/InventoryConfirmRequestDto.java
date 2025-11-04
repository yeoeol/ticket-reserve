package ticket.reserve.reservation.client.dto;

public record InventoryConfirmRequestDto(
        Long eventId,
        Long inventoryId
) {
}

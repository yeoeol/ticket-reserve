package ticket.reserve.reservation.dto;

public record InventoryConfirmRequestDto(
        Long eventId,
        Long inventoryId
) {
}

package ticket.reserve.reservation.application.dto.request;

public record InventoryHoldRequestDto(
        Long eventId,
        Long inventoryId
) {
}

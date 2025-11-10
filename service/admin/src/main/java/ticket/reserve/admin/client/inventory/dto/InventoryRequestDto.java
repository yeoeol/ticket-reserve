package ticket.reserve.admin.client.inventory.dto;

public record InventoryRequestDto(
        Long eventId,
        int price
) {
}

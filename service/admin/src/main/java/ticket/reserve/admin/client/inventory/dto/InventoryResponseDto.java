package ticket.reserve.admin.client.inventory.dto;

import lombok.Builder;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long eventId,
        int price,
        String status
) {
}

package ticket.reserve.admin.client.inventory.dto;

import lombok.Builder;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long eventId,
        String inventoryName,
        int price,
        String status
) {
    public InventoryResponseDto(Long eventId) {
        this(null, eventId, "", 0, "");
    }
}

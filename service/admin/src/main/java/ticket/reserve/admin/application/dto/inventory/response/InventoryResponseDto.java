package ticket.reserve.admin.application.dto.inventory.response;

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

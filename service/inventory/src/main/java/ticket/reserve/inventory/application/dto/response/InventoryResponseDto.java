package ticket.reserve.inventory.application.dto.response;

import lombok.Builder;
import ticket.reserve.inventory.domain.Inventory;

@Builder
public record InventoryResponseDto(
        Long inventoryId,
        Long buskingId,
        String inventoryName,
        Integer price,
        String status
) {
    public static InventoryResponseDto from(Inventory inventory) {
        return InventoryResponseDto.builder()
                .inventoryId(inventory.getId())
                .buskingId(inventory.getBuskingId())
                .inventoryName(inventory.getInventoryName())
                .price(inventory.getPrice())
                .status(inventory.getStatus().name())
                .build();
    }
}

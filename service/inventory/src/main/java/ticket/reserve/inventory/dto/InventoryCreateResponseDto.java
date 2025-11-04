package ticket.reserve.inventory.dto;

import lombok.Builder;

@Builder
public record InventoryCreateResponseDto(
        EventResponseDto event,
        InventoryResponseDto inventory
) {
    public static InventoryCreateResponseDto of(EventResponseDto event, InventoryResponseDto inventory) {
        return InventoryCreateResponseDto.builder()
                .event(event)
                .inventory(inventory)
                .build();
    }
}

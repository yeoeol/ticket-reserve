package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.client.dto.EventResponseDto;

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

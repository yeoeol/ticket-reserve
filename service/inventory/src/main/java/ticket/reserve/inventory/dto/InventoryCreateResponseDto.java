package ticket.reserve.inventory.dto;

import lombok.Builder;
import ticket.reserve.inventory.client.dto.EventDetailResponseDto;

@Builder
public record InventoryCreateResponseDto(
        EventDetailResponseDto event,
        InventoryResponseDto inventory
) {
    public static InventoryCreateResponseDto of(EventDetailResponseDto event, InventoryResponseDto inventory) {
        return InventoryCreateResponseDto.builder()
                .event(event)
                .inventory(inventory)
                .build();
    }
}

package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record InventoryHoldRequestDto(
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{inventory.id.not_null}")
        Long inventoryId
) {
}

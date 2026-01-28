package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryUpdateRequestDto(
        @NotBlank(message = "{inventory.name.not_null}")
        String inventoryName,
        @NotNull(message = "{inventory.price.not_null}")
        @PositiveOrZero(message = "{inventory.price.range}")
        Integer price
) {
}

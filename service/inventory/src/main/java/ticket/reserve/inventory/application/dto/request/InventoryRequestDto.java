package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.core.tsid.IdGenerator;

public record InventoryRequestDto(
        @NotBlank(message = "{inventory.name.not_null}")
        String inventoryName,
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{inventory.price.not_null}")
        @PositiveOrZero(message = "{inventory.price.range}")
        Integer price
) {
    public Inventory toEntity(IdGenerator idGenerator) {
        return Inventory.create(idGenerator, this.buskingId, this.inventoryName, this.price);
    }
}

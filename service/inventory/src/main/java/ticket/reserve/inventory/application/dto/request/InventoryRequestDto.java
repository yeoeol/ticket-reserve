package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.tsid.IdGenerator;

public record InventoryRequestDto(
        @NotBlank(message = "좌석 이름은 필수입니다.")
        String inventoryName,
        @NotNull(message = "버스킹ID는 필수입니다.")
        Long buskingId,
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer price
) {
    public Inventory toEntity(IdGenerator idGenerator) {
        return Inventory.create(idGenerator, this.buskingId, this.inventoryName, this.price);
    }
}

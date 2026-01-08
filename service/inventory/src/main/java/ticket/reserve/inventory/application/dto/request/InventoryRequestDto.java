package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ticket.reserve.inventory.domain.Inventory;

public record InventoryRequestDto(
        @NotBlank(message = "좌석 이름은 필수입니다.")
        String inventoryName,
        @NotNull(message = "이벤트ID는 필수입니다.")
        Long eventId,
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        int price
) {
    public Inventory toEntity() {
        return Inventory.builder()
                .inventoryName(this.inventoryName)
                .eventId(this.eventId)
                .price(this.price)
                .build();
    }
}

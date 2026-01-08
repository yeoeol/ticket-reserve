package ticket.reserve.inventory.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record InventoryHoldRequestDto(
        @NotNull(message = "이벤트ID는 필수입니다.")
        Long eventId,
        @NotNull(message = "좌석ID는 필수입니다.")
        Long inventoryId
) {
}

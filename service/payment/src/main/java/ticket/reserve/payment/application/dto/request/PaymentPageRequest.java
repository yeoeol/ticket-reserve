package ticket.reserve.payment.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentPageRequest(
        @NotNull(message = "사용자ID는 필수입니다.")
        Long userId,
        @NotNull(message = "예매ID는 필수입니다.")
        Long reservationId,
        @NotNull(message = "좌석ID는 필수입니다.")
        Long inventoryId,
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer amount
) {
}

package ticket.reserve.payment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentConfirmRequestDto(
        @NotBlank(message = "주문ID는 필수입니다.")
        String orderId,
        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey,
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer amount
) {
}

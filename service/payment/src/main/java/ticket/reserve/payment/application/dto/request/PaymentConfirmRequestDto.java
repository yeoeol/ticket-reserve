package ticket.reserve.payment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentConfirmRequestDto(
        @NotBlank(message = "{payment.orderId.not_blank}")
        String orderId,
        @NotBlank(message = "{payment.paymentKey.not_blank}")
        String paymentKey,
        @NotNull(message = "{payment.amount.not_null}")
        @PositiveOrZero(message = "{payment.amount.range}")
        Integer amount
) {
}

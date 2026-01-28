package ticket.reserve.payment.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentPageRequest(
        @NotNull(message = "{user.id.not_null}")
        Long userId,
        @NotNull(message = "{reservation.id.not_null}")
        Long reservationId,
        @NotNull(message = "{inventory.id.not_null}")
        Long inventoryId,
        @NotNull(message = "{payment.amount.not_null}")
        @PositiveOrZero(message = "{payment.amount.range}")
        Integer amount
) {
}

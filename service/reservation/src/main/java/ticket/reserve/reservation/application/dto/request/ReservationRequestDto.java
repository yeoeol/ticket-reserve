package ticket.reserve.reservation.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;

public record ReservationRequestDto(
        @NotNull(message = "이벤트ID는 필수입니다.")
        Long eventId,
        @NotNull(message = "좌석ID는 필수입니다.")
        Long inventoryId,
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer price
) {
    public Reservation toEntity(Long userId) {
        return Reservation.builder()
                .userId(userId)
                .eventId(this.eventId)
                .inventoryId(this.inventoryId)
                .price(this.price)
                .status(ReservationStatus.PENDING)  // "임시 선점"
                .build();
    }
}

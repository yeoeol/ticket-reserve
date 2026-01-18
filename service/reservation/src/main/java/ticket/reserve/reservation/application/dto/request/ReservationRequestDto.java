package ticket.reserve.reservation.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.tsid.IdGenerator;

public record ReservationRequestDto(
        @NotNull(message = "버스킹ID는 필수입니다.")
        Long buskingId,
        @NotNull(message = "좌석ID는 필수입니다.")
        Long inventoryId,
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer price
) {
    public Reservation toEntity(IdGenerator idGenerator, Long userId) {
        return Reservation.create(
                idGenerator, userId, this.buskingId, this.inventoryId, this.price
        );
    }
}

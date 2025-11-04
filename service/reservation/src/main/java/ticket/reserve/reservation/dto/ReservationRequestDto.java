package ticket.reserve.reservation.dto;

import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;

public record ReservationRequestDto(
        Long eventId,
        Long inventoryId,
        int price
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

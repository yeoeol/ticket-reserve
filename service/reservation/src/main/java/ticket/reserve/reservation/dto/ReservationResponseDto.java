package ticket.reserve.reservation.dto;

import lombok.Builder;

@Builder
public record ReservationResponseDto(
        Long reservationId,
        Long inventoryId,
        int price
) {
    public static ReservationResponseDto of(Long reservationId, Long inventoryId, int price) {
        return ReservationResponseDto.builder()
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .price(price)
                .build();
    }
}

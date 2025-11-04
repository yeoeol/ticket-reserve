package ticket.reserve.reservation.dto;

import lombok.Builder;

@Builder
public record ReservationResponseDto(
        Long reservationId,
        int price
) {
    public static ReservationResponseDto of(Long reservationId, int price) {
        return ReservationResponseDto.builder()
                .reservationId(reservationId)
                .price(price)
                .build();
    }
}

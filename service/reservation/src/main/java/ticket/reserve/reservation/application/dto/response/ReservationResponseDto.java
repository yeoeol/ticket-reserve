package ticket.reserve.reservation.application.dto.response;

import lombok.Builder;

@Builder
public record ReservationResponseDto(
        Long reservationId,
        Long inventoryId,
        Integer price
) {
    public static ReservationResponseDto of(Long reservationId, Long inventoryId, Integer price) {
        return ReservationResponseDto.builder()
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .price(price)
                .build();
    }
}

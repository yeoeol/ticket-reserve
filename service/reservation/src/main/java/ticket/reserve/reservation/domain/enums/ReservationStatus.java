package ticket.reserve.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("임시 선점"),
    CONFIRMED("예매 확정"),
    SELECTED("선택됨")
    ;

    private final String description;
}

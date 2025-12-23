package ticket.reserve.common.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.common.event.EventPayload;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationExpiredPayload implements EventPayload {
    private Long reservationId;
    private Long inventoryId;
    private Long eventId;
    private Long userId;
}

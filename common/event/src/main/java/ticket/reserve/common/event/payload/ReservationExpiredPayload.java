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
    Long reservationId;
    Long inventoryId;
    Long eventId;
    Long userId;
}

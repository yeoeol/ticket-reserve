package ticket.reserve.core.event.payload;

import lombok.*;
import ticket.reserve.core.event.EventPayload;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationExpiredPayload implements EventPayload {
    private Long reservationId;
    private Long inventoryId;
    private Long buskingId;
    private Long userId;
}

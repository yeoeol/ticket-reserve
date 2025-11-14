package ticket.reserve.common.event.payload;

import lombok.*;
import ticket.reserve.common.event.EventPayload;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmedEventPayload implements EventPayload {

    private Long reservationId;
    private Long inventoryId;

    private Long userId;
    private String orderId;
    private int totalAmount;
}

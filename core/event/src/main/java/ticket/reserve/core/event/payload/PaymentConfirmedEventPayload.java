package ticket.reserve.core.event.payload;

import lombok.*;
import ticket.reserve.core.event.EventPayload;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentConfirmedEventPayload implements EventPayload {

    private Long reservationId;
    private Long inventoryId;

    private Long userId;
    private String orderId;
    private int totalAmount;
}

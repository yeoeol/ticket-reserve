package ticket.reserve.payment.client.dto;

import lombok.*;
import ticket.reserve.payment.domain.Payment;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmedEvent {

    private Long reservationId;
    private Long inventoryId;

    private Long userId;
    private String orderId;
    private int totalAmount;

    public static PaymentConfirmedEvent from(Payment payment) {
        return PaymentConfirmedEvent.builder()
                .reservationId(payment.getReservationId())
                .inventoryId(payment.getInventoryId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .build();
    }
}

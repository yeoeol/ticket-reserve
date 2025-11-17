package ticket.reserve.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    private Long userId;
    private Long reservationId;
    private Long inventoryId;

    private String paymentKey;
    private String orderId;
    private String orderName;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private int totalAmount;


    public void confirmPayment(
            String paymentKey,
            String orderName,
            String status,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt,
            int totalAmount
    ) {
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
    }
}

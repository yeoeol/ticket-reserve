package ticket.reserve.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment {

    @Id
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
    private Integer totalAmount;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(IdGenerator idGenerator, Long userId, Long reservationId, Long inventoryId,
                    String paymentKey, String orderId, String orderName, String status,
                    LocalDateTime requestedAt, LocalDateTime approvedAt, Integer totalAmount
    ) {
        this.id = idGenerator.nextId();
        this.userId = userId;
        this.reservationId = reservationId;
        this.inventoryId = inventoryId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
    }

    public static Payment create(IdGenerator idGenerator, Long userId, Long reservationId, Long inventoryId, String orderId) {
        return Payment.builder()
                .idGenerator(idGenerator)
                .userId(userId)
                .reservationId(reservationId)
                .inventoryId(inventoryId)
                .orderId(orderId)
                .build();
    }

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

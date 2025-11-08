package ticket.reserve.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.payment.client.dto.TossResponseDto;

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

    private String paymentKey;
    private String orderId;
    private String orderName;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private int totalAmount;

    public void setting(TossResponseDto tossResponseDto) {
        this.paymentKey = tossResponseDto.paymentKey();
        this.orderName = tossResponseDto.orderName();
        this.status = tossResponseDto.status();
        this.requestedAt = tossResponseDto.requestedAt().toLocalDateTime();
        this.approvedAt = tossResponseDto.approvedAt().toLocalDateTime();
        this.totalAmount = tossResponseDto.totalAmount();
    }
}

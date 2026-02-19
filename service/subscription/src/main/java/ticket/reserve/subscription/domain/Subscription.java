package ticket.reserve.subscription.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "subscription",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"busking_id", "user_id"}
                )
        }
)
public class Subscription extends BaseTimeEntity {

    @Id
    @Column(name = "subscription_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long buskingId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;  // 구독 여부

    private boolean isNotified;         // 알림 발송 여부

    @Builder(access = AccessLevel.PRIVATE)
    private Subscription(IdGenerator idGenerator, Long userId, Long buskingId, LocalDateTime startTime, SubscriptionStatus status, boolean isNotified) {
        this.id = idGenerator.nextId();
        this.userId = userId;
        this.buskingId = buskingId;
        this.startTime = startTime;
        this.status = status;
        this.isNotified = isNotified;
    }

    public static Subscription create(
            IdGenerator idGenerator, Long userId, Long buskingId,
            LocalDateTime startTime, SubscriptionStatus status
    ) {
        return Subscription.builder()
                .idGenerator(idGenerator)
                .userId(userId)
                .buskingId(buskingId)
                .startTime(startTime)
                .status(status)
                .isNotified(false)
                .build();
    }

    public void activate() {
        this.status = SubscriptionStatus.ACTIVATED;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    public void notified() {
        this.isNotified = true;
    }
}

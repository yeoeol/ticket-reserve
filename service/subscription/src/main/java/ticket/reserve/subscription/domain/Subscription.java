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
@Table(name = "subscription")
public class Subscription {

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
    private SubscriptionStatus status;

    private boolean notified;

    @Builder(access = AccessLevel.PRIVATE)
    private Subscription(IdGenerator idGenerator, Long userId, Long buskingId, LocalDateTime startTime, SubscriptionStatus status, boolean notified) {
        this.id = idGenerator.nextId();
        this.userId = userId;
        this.buskingId = buskingId;
        this.startTime = startTime;
        this.status = status;
        this.notified = notified;
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
                .notified(false)
                .build();
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }
}

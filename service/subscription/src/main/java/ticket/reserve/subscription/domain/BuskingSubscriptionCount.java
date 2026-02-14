package ticket.reserve.subscription.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "busking_subscription_count")
public class BuskingSubscriptionCount {

    @Id
    @Column(name = "busking_id")
    private Long id;

    private Long subscriptionCount;

    @Builder(access = AccessLevel.PRIVATE)
    private BuskingSubscriptionCount(Long buskingId, Long subscriptionCount) {
        this.id = buskingId;
        this.subscriptionCount = subscriptionCount;
    }

    public static BuskingSubscriptionCount create(
            Long buskingId, Long subscriptionCount
    ) {
        return BuskingSubscriptionCount.builder()
                .buskingId(buskingId)
                .subscriptionCount(subscriptionCount)
                .build();
    }
}

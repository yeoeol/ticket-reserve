package ticket.reserve.subscription.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ticket.reserve.subscription.domain.BuskingSubscriptionCount;

public interface BuskingSubscriptionCountRepository extends JpaRepository<BuskingSubscriptionCount, Long> {
    @Query(
            value = "update busking_subscription_count " +
                    "set subscription_count = subscription_count + 1 " +
                    "where busking_id = :buskingId",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("buskingId") Long buskingId);

    @Query(
            value = "update busking_subscription_count " +
                    "set subscription_count = subscription_count - 1 " +
                    "where busking_id = :buskingId",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("buskingId") Long buskingId);
}

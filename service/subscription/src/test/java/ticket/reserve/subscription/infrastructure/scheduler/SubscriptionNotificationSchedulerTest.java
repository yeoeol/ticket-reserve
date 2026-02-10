package ticket.reserve.subscription.infrastructure.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionNotificationSentEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.infrastructure.persistence.SubscriptionRedisAdapter;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionNotificationSchedulerTest {

    @InjectMocks
    private SubscriptionNotificationScheduler scheduler;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;

    @Mock
    private SubscriptionRedisAdapter subscriptionRedisAdapter;

    @Test
    @DisplayName("알림 대상이 존재하면 이벤트를 발행하고 Redis 데이터를 삭제해야 한다")
    void scheduler_success() {
        //given
        Long buskingId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(30);
        Set<Long> userIds = Set.of(10L, 20L);

        BuskingNotificationTarget target = new BuskingNotificationTarget(buskingId, startTime);

        given(subscriptionRedisAdapter.findTargetsToNotify(any(LocalDateTime.class)))
                .willReturn(Set.of(target));
        given(subscriptionRedisAdapter.findSubscribersByBuskingId(buskingId))
                .willReturn(userIds);

        //when
        scheduler.subscriptionNotificationScheduler();

        //then
        verify(outboxEventPublisher, times(1))
                .publish(
                        eq(EventType.SUBSCRIPTION_NOTIFICATION_SENT),
                        argThat(payload -> {
                            SubscriptionNotificationSentEventPayload p = (SubscriptionNotificationSentEventPayload) payload;
                            return p.getBuskingId().equals(buskingId) &&
                                    p.getUserIds().equals(userIds) &&
                                    p.getRemainingMinutes() <= 30;
                        }),
                        eq(buskingId)
                );
        verify(subscriptionRedisAdapter, times(1))
                .removeSubscriptionData(buskingId);
    }

}
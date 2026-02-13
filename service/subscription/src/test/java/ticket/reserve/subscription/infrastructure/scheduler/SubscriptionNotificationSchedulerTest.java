package ticket.reserve.subscription.infrastructure.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.subscription.application.NotificationPublishService;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;

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
    private NotificationPublishService notificationPublishService;
    @Mock
    private NotificationSchedulePort notificationSchedulePort;
    @Mock
    private SubscriptionQueryService subscriptionQueryService;

    @Test
    @DisplayName("알림 대상이 존재하면 이벤트를 발행하고 알림 스케줄 데이터를 삭제해야 한다")
    void scheduler_success() {
        //given
        Long buskingId1 = 1L;
        Long buskingId2 = 2L;
        Long buskingId3 = 3L;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(30);
        Set<Long> userIds1 = Set.of(10L, 20L);
        Set<Long> userIds2 = Set.of(30L, 40L);
        Set<Long> userIds3 = Set.of(50L, 60L);

        Set<BuskingNotificationTarget> targets = Set.of(
                new BuskingNotificationTarget(buskingId1, startTime),
                new BuskingNotificationTarget(buskingId2, startTime),
                new BuskingNotificationTarget(buskingId3, startTime)
        );

        given(notificationSchedulePort.findTargetsToNotify(any(LocalDateTime.class)))
                .willReturn(targets);

        given(subscriptionQueryService.findSubscribers(buskingId1)).willReturn(userIds1);
        given(subscriptionQueryService.findSubscribers(buskingId2)).willReturn(userIds2);
        given(subscriptionQueryService.findSubscribers(buskingId3)).willReturn(userIds3);

        //when
        scheduler.subscriptionNotificationScheduler();

        //then
        verify(notificationPublishService, times(3))
                .publishNotificationEvent(anyLong(), anySet(), anyLong());
        verify(notificationSchedulePort, times(3))
                .removeFromNotificationSchedule(anyLong());
    }

}
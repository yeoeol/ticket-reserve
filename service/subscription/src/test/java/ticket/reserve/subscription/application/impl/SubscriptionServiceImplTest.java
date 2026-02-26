package ticket.reserve.subscription.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.SubscriptionUseCase;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;
import ticket.reserve.subscription.application.port.out.BuskingPort;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.BuskingSubscriptionCountRepository;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionQueryService subscriptionQueryService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;
    @Mock
    private BuskingPort buskingPort;
    @Mock
    private BuskingInfoPort buskingInfoPort;
    @Mock
    private NotificationSchedulePort notificationSchedulePort;
    @Mock
    private SubscriptionUseCase subscriptionUseCase;

    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscription = Subscription.create(
                () -> 1L,
                2L,
                3L,
                LocalDateTime.of(2026, 1, 1, 12, 30),
                SubscriptionStatus.ACTIVATED
        );
    }

    @Test
    @DisplayName("조회한 구독 엔티티 리스트 요소들을 알림 여부 true로 모두 변경한다.")
    void notified_success() {
        //given
        Set<Long> userIds = Set.of(2L);
        given(subscriptionRepository.findAllByBuskingIdAndUserIdIn(3L, userIds))
                .willReturn(List.of(subscription));

        //when
        subscriptionService.notified(3L, userIds);

        //then
        assertThat(subscription.isNotified()).isTrue();
    }
}
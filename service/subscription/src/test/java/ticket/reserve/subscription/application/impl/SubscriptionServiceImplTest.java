package ticket.reserve.subscription.application.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.port.out.BuskingPort;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private SubscriptionQueryService subscriptionQueryService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private BuskingPort buskingPort;

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
    @DisplayName("구독 내역이 이미 존재한다면 활성 상태로 업데이트한다.")
    void subscribe_success_1() {
        //given
        SubscriptionRequestDto requestDto =
                new SubscriptionRequestDto(3L, 2L, LocalDateTime.of(2026, 1, 1, 12, 30));
        subscription.cancel();
        given(subscriptionRepository.findByBuskingIdAndUserIdForUpdate(3L, 2L))
                .willReturn(Optional.of(subscription));

        //when
        subscriptionService.subscribe(requestDto);

        //then
        verify(subscriptionRepository, times(0)).save(any(Subscription.class));
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVATED);
    }

    @Test
    @DisplayName("구독 내역이 존재하지 않는다면 구독 엔티티를 저장한다.")
    void subscribe_success_2() {
        //given
        SubscriptionRequestDto requestDto =
                new SubscriptionRequestDto(3L, 2L, LocalDateTime.of(2026, 1, 1, 12, 30));

        given(subscriptionRepository.findByBuskingIdAndUserIdForUpdate(3L, 2L))
                .willReturn(Optional.empty());

        //when
        subscriptionService.subscribe(requestDto);

        //then
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    @DisplayName("구독 엔티티를 조회한 후 취소 상태로 변경한다.")
    void unsubscribe_success() {
        //given
        SubscriptionCancelRequestDto requestDto =
                new SubscriptionCancelRequestDto(3L, 2L);

        given(subscriptionQueryService.getSubscription(3L, 2L))
                .willReturn(subscription);

        //when
        subscriptionService.unsubscribe(requestDto);

        //then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
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
package ticket.reserve.subscription.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.SubscriptionCancelledEventPayload;
import ticket.reserve.core.event.payload.SubscriptionCreatedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.impl.SubscriptionServiceImpl;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;
import ticket.reserve.subscription.application.port.out.BuskingPort;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;
import ticket.reserve.subscription.domain.BuskingSubscriptionCount;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;
import ticket.reserve.subscription.domain.repository.BuskingSubscriptionCountRepository;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionUseCaseTest {

    @InjectMocks
    private SubscriptionUseCase subscriptionUseCase;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private SubscriptionQueryService subscriptionQueryService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;
    @Mock
    private OutboxEventPublisher outboxEventPublisher;

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
    void subscribeProcess_success_1() {
        //given
        SubscriptionRequestDto requestDto =
                new SubscriptionRequestDto(3L, 2L, LocalDateTime.of(2026, 1, 1, 12, 30));
        subscription.cancel();
        given(subscriptionRepository.findByBuskingIdAndUserIdForUpdate(3L, 2L))
                .willReturn(Optional.of(subscription));
        given(buskingSubscriptionCountRepository.increase(anyLong())).willReturn(1);

        //when
        subscriptionUseCase.subscribeProcess(requestDto, "title1", "location1");

        //then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVATED);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
        verify(buskingSubscriptionCountRepository, never()).save(any(BuskingSubscriptionCount.class));
        verify(outboxEventPublisher, times(1)).publish(
                eq(EventType.SUBSCRIPTION_CREATED),
                any(SubscriptionCreatedEventPayload.class),
                anyLong()
        );
    }

    @Test
    @DisplayName("구독 내역이 존재하지 않는다면 구독 엔티티를 저장한다.")
    void subscribeProcess_success_2() {
        //given
        SubscriptionRequestDto requestDto =
                new SubscriptionRequestDto(3L, 2L, LocalDateTime.of(2026, 1, 1, 12, 30));
        given(subscriptionRepository.findByBuskingIdAndUserIdForUpdate(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        given(subscriptionRepository.save(any())).willReturn(subscription);
        given(buskingSubscriptionCountRepository.increase(anyLong())).willReturn(0);

        //when
        subscriptionUseCase.subscribeProcess(requestDto, "title1", "location1");

        //then
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
        verify(buskingSubscriptionCountRepository, times(1)).save(any(BuskingSubscriptionCount.class));
        verify(outboxEventPublisher, times(1)).publish(
                eq(EventType.SUBSCRIPTION_CREATED),
                any(SubscriptionCreatedEventPayload.class),
                anyLong()
        );
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
        subscriptionUseCase.unsubscribeProcess(requestDto);

        //then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(buskingSubscriptionCountRepository, times(1)).decrease(anyLong());
        verify(outboxEventPublisher, times(1)).publish(
                eq(EventType.SUBSCRIPTION_CANCELLED),
                any(SubscriptionCancelledEventPayload.class),
                anyLong()
        );
    }
}
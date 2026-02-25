package ticket.reserve.subscription.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.SubscriptionService;
import ticket.reserve.subscription.application.SubscriptionUseCase;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;
import ticket.reserve.subscription.application.port.out.BuskingPort;
import ticket.reserve.subscription.application.port.out.NotificationSchedulePort;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.repository.BuskingSubscriptionCountRepository;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.Set;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;
    private final BuskingSubscriptionCountRepository buskingSubscriptionCountRepository;
    private final BuskingPort buskingPort;
    private final BuskingInfoPort buskingInfoPort;
    private final NotificationSchedulePort notificationSchedulePort;
    private final SubscriptionUseCase subscriptionUseCase;

    @Override
    public void subscribe(SubscriptionRequestDto request) {
        BuskingNotificationTarget target = buskingInfoPort.read(request.buskingId());
        String title = target != null ? target.title() : "";
        String location = target != null ? target.location() : "";

        subscriptionUseCase.subscribeProcess(request, title, location);
    }

    @Override
    public void unsubscribe(SubscriptionCancelRequestDto request) {
        subscriptionUseCase.unsubscribeProcess(request);
    }

    @Override
    @Transactional
    public void notified(Long buskingId, Set<Long> userIds) {
        subscriptionRepository.findAllByBuskingIdAndUserIdIn(buskingId, userIds)
                        .forEach(Subscription::notified);
    }

    @Override
    public List<BuskingResponseDto> getAllByUserId(Long userId) {
        // userId에 대해 ACTIVATED 상태인 것들의 buskingId를 조회
        List<Long> buskingIds = subscriptionQueryService.findBuskingIdsByUserIdWithActivated(userId);

        return buskingPort.getAllByBuskingIds(buskingIds).stream()
                .sorted(comparing(BuskingResponseDto::startTime).reversed())
                .map(busking -> busking.withSubscribed(true))
                .toList();
    }

    @Override
    public void removeSubscriptionData(Long buskingId) {
        // 알림 스케줄 목록에서 삭제
        notificationSchedulePort.removeFromNotificationSchedule(buskingId);
        // 버스킹 정보 삭제
        buskingInfoPort.delete(buskingId);
        // 구독 정보 삭제 (DB)
        subscriptionRepository.deleteAllByBuskingId(buskingId);
        // 구독 개수 정보 삭제 (DB)
        buskingSubscriptionCountRepository.deleteById(buskingId);
    }
}

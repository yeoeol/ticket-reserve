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
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.Set;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;
    private final BuskingPort buskingPort;
    private final BuskingInfoPort buskingInfoPort;
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
}

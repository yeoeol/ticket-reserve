package ticket.reserve.subscription.application;

import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;

import java.util.List;
import java.util.Set;

public interface SubscriptionService {
    // 구독 로직
    void subscribe(SubscriptionRequestDto request);

    // 구독 취소 로직
    void unsubscribe(SubscriptionCancelRequestDto request);

    // 알림 전송 완료 체크
    void notified(Long buskingId, Set<Long> userIds);

    // 구독한 버스킹 목록 조회
    List<BuskingResponseDto> getAllByUserId(Long userId);
}

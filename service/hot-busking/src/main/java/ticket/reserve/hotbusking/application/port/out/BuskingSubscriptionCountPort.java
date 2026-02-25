package ticket.reserve.hotbusking.application.port.out;

import java.time.Duration;

public interface BuskingSubscriptionCountPort {
    /**
     * 구독 개수 데이터 저장 또는 업데이트
     */
    void createOrUpdate(Long buskingId, Long subscriptionCount, Duration ttl);
    /**
     * 구독 개수 데이터 읽기
     */
    Long read(Long buskingId);
    /**
     * 구독 개수 데이터 삭제
     */
    void remove(Long buskingId);
}

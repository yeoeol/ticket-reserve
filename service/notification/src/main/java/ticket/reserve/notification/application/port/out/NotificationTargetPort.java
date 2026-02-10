package ticket.reserve.notification.application.port.out;

import java.util.List;

public interface NotificationTargetPort {
    /**
     * 버스킹 위치 기준 반경 내에 있는 활성 상태의 사용자 목록을 조회한다.
     */
    List<Long> findNearbyActiveUsers(Double lng, Double lat, double radiusKm);
}

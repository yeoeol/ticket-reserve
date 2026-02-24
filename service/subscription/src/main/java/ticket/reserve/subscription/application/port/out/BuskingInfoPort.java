package ticket.reserve.subscription.application.port.out;

import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;

import java.time.LocalDateTime;

public interface BuskingInfoPort {
    /**
     * Key : buskingId, Value : BuskingNotificationTarget.class 직렬화
     */
    void createOrUpdate(Long buskingId, String title, String location, LocalDateTime startTime);
    /**
     *  버스킹 정보 조회
     */
    BuskingNotificationTarget read(Long buskingId);
    /**
     * 버스킹 정보 삭제
     */
    void delete(Long buskingId);
}

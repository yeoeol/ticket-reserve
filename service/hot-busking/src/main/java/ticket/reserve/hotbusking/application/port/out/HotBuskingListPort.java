package ticket.reserve.hotbusking.application.port.out;

import java.time.Duration;
import java.util.List;

public interface HotBuskingListPort {
    /**
     * 인기 버스킹 저장
     */
    void add(Long buskingId, Long score, Long limit, Duration ttl);
    /**
     * 인기 버스킹 목록 읽기
     */
    List<Long> readAll();
    /**
     * 인기 버스킹 삭제
     */
    void remove(Long buskingId);
}

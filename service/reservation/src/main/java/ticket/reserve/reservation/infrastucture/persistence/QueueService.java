package ticket.reserve.reservation.infrastucture.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.dto.response.QueueStatusResponseDto;
import ticket.reserve.reservation.application.port.out.EventPort;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EventPort eventPort;

    private static final long ALLOW_COUNT = 10L;       // 한 번에 입장시킬 인원 수 (조절 가능)
    private static final long ACTIVE_TTL = 2*60*1000L;  // ACTIVE 상태 유지 시간 (2분 뒤 만료)

    private static final String WAITING_KEY = "queue:waiting:%d";
    private static final String ACTIVE_KEY = "queue:active:%d";

    // 대기열 등록
    public QueueStatusResponseDto registerWaitingQueue(Long eventId, String userId) {
        String activeKey = String.format(ACTIVE_KEY, eventId);
        String waitingKey = String.format(WAITING_KEY, eventId);

        if (redisTemplate.opsForZSet().score(activeKey, userId) != null) {
            return QueueStatusResponseDto.active();
        }

        long currentTimeMills = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(waitingKey, userId, currentTimeMills);

        Long rank = redisTemplate.opsForZSet().rank(waitingKey, userId);
        return QueueStatusResponseDto.waiting(rank);
    }

    // 진입 가능 여부 반환
    public boolean isAllowed(Long eventId, Long userId) {
        return redisTemplate.opsForZSet().score(String.format(ACTIVE_KEY, eventId), String.valueOf(userId)) != null;
    }

    // 앞의 대기 인원 수 조회
    public QueueStatusResponseDto getQueueStatus(Long eventId, Long userId) {
        String activeKey = String.format(ACTIVE_KEY, eventId);
        String waitingKey = String.format(WAITING_KEY, eventId);

        Double activeScore = redisTemplate.opsForZSet().score(activeKey, String.valueOf(userId));
        if (activeScore != null) {
            return QueueStatusResponseDto.active();
        }

        Long rank = redisTemplate.opsForZSet().rank(waitingKey, String.valueOf(userId));
        if (rank != null) {
            return QueueStatusResponseDto.waiting(rank+1);
        }

        throw new CustomException(ErrorCode.QUEUE_TOKEN_NOT_FOUND);
    }

    // 스케줄링 메서드 - 3초마다 실행
    public void processQueue() {
        List<Long> eventIds = eventPort.getEventIds();
        long currentTimeMillis = System.currentTimeMillis();

        for (Long eventId : eventIds) {
            String waitingKey = String.format(WAITING_KEY, eventId);
            String activeKey = String.format(ACTIVE_KEY, eventId);

            // 만료 시간이 현재 시간보다 작은 사용자들의 ACTIVE 상태를 삭제
            redisTemplate.opsForZSet().removeRangeByScore(activeKey, 0, currentTimeMillis);

            // 대기열에서 선착순 ALLOW_COUNT 수만큼 조회
            Set<String> usersToEnter = redisTemplate.opsForZSet().range(waitingKey, 0, ALLOW_COUNT - 1);
            if (usersToEnter == null || usersToEnter.isEmpty()) continue;

            log.info("[QueueService.enterUser] {}명 입장 처리 중...", usersToEnter.size());

            // 입장열로 이동
            long expireTime = System.currentTimeMillis() + ACTIVE_TTL;
            for (String userId : usersToEnter) {
                redisTemplate.opsForZSet().add(activeKey, userId, expireTime);
                redisTemplate.opsForZSet().remove(waitingKey, userId);
            }
        }
    }
}

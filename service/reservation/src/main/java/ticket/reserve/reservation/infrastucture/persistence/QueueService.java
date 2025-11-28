package ticket.reserve.reservation.infrastucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.dto.response.QueueStatusResponseDto;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

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
}

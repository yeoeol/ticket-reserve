package ticket.reserve.reservation.infrastucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String WAITING_KEY = "queue:waiting:%d";
    private static final String ACTIVE_KEY = "queue:active:%d";

    // 대기열 등록
    public Long registerWaitingQueue(Long eventId, Long userId) {
        String key = String.format(WAITING_KEY, eventId);

        long currentTimeMills = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, String.valueOf(userId), currentTimeMills);

        return redisTemplate.opsForZSet().rank(key, String.valueOf(userId));
    }

    // 진입 가능 여부 반환
    public boolean isAllowed(Long eventId, Long userId) {
        return redisTemplate.opsForZSet().score(String.format(ACTIVE_KEY, eventId), String.valueOf(userId)) != null;
    }

    // 앞의 대기 인원 수 조회
    public Long getRank(Long eventId, Long userId) {
        return redisTemplate.opsForZSet().rank(String.format(WAITING_KEY, eventId), String.valueOf(userId));
    }
}

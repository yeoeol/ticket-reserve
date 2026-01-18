package ticket.reserve.reservation.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACTIVE_KEY = "queue:active:%d";
    private static final String WAITING_KEY = "queue:waiting:%d";

    public void addToActiveQueue(Long buskingId, String userId, long score) {
        redisTemplate.opsForZSet()
                .add(generateActiveKey(buskingId), userId, score);
    }

    public void addToWaitingQueue(Long buskingId, String userId, long score) {
        redisTemplate.opsForZSet()
                .add(generateWaitingKey(buskingId), userId, score);
    }

    public Double getActiveScore(Long buskingId, String userId) {
        return redisTemplate.opsForZSet()
                .score(generateActiveKey(buskingId), userId);
    }

    public Long getWaitingRank(Long buskingId, String userId) {
        return redisTemplate.opsForZSet()
                .rank(generateWaitingKey(buskingId), userId);
    }

    public void removeActiveBeforeExpiryTime(Long buskingId, long currentTime) {
        redisTemplate.opsForZSet().removeRangeByScore(generateActiveKey(buskingId), 0, currentTime);
    }

    public void removeToWaitingQueue(Long buskingId, String userId) {
        redisTemplate.opsForZSet().remove(generateWaitingKey(buskingId), userId);
    }

    public Set<String> getOldestWaitingUsers(Long buskingId, long count) {
        return redisTemplate.opsForZSet().range(generateWaitingKey(buskingId), 0, count-1);
    }


    private String generateActiveKey(Long id) {
        return ACTIVE_KEY.formatted(id);
    }

    private String generateWaitingKey(Long id) {
        return WAITING_KEY.formatted(id);
    }
}

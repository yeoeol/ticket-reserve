package ticket.reserve.reservation.infrastucture.persistence;

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

    public void addToWaitingQueue(Long eventId, String userId, long score) {
        redisTemplate.opsForZSet()
                .add(generateWaitingKey(eventId), userId, score);
    }

    public Double getActiveScore(Long eventId, String userId) {
        return redisTemplate.opsForZSet()
                .score(generateActiveKey(eventId), userId);
    }

    public Long getWaitingRank(Long eventId, String userId) {
        return redisTemplate.opsForZSet()
                .rank(generateWaitingKey(eventId), userId);
    }

    public void removeActiveBeforeExpiryTime(Long eventId, long currentTime) {
        redisTemplate.opsForZSet().removeRangeByScore(generateActiveKey(eventId), 0, currentTime);
    }

    public void removeToWaitingQueue(Long eventId, String userId) {
        redisTemplate.opsForZSet().remove(generateWaitingKey(eventId), userId);
    }

    public Set<String> getOldestWaitingUsers(Long eventId, long count) {
        return redisTemplate.opsForZSet().range(generateWaitingKey(eventId), 0, count-1);
    }


    private String generateActiveKey(Long id) {
        return ACTIVE_KEY.formatted(id);
    }

    private String generateWaitingKey(Long id) {
        return WAITING_KEY.formatted(id);
    }
}

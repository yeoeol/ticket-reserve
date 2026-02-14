package ticket.reserve.hotbusking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class HotBuskingListRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.hot-busking-list-key:hot-busking:list}")
    private String hotBuskingListKey;

    public void add(Long buskingId, Long score, Long limit, Duration ttl) {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            conn.zAdd(hotBuskingListKey, score, String.valueOf(buskingId));
            conn.zRemRange(hotBuskingListKey, 0, -limit-1);
            conn.expire(hotBuskingListKey, ttl.toSeconds());
            return null;
        });
    }
}

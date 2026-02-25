package ticket.reserve.hotbusking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import ticket.reserve.hotbusking.application.port.out.HotBuskingListPort;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HotBuskingListRepository implements HotBuskingListPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.hot-busking-list-key:hot-busking:list}")
    private String hotBuskingListKey;

    public void add(Long buskingId, Long score, Long limit, Duration ttl) {
        redisTemplate.executePipelined((RedisCallback<?>) conn -> {
            byte[] key = hotBuskingListKey.getBytes();
            byte[] value = String.valueOf(buskingId).getBytes();

            conn.zAdd(key, score, value);
            conn.zSetCommands().zRemRange(key, 0, -(limit+1));
            conn.keyCommands().expire(key, ttl.toSeconds());
            return null;
        });
    }

    public List<Long> readAll() {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(hotBuskingListKey, 0, -1).stream()
                .peek(tuple ->
                    log.info("[HotBuskingListRepository.readAll] buskingId={}, score={}", tuple.getValue(), tuple.getScore()))
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Long::valueOf)
                .toList();
    }

    public void remove(Long buskingId) {
        redisTemplate.opsForZSet().remove(hotBuskingListKey, String.valueOf(buskingId));
    }
}

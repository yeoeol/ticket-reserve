package ticket.reserve.hotbusking.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.hotbusking.application.port.out.BuskingSubscriptionCountPort;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class BuskingSubscriptionCountRepository implements BuskingSubscriptionCountPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.hot-busking-subscription-count-key:hot-busking:busking:%s:subscription-count}")
    private String subscriptionCountKey;

    public void createOrUpdate(Long buskingId, Long subscriptionCount, Duration ttl) {
        redisTemplate.opsForValue().set(generateKey(buskingId), String.valueOf(subscriptionCount), ttl);
    }

    public Long read(Long buskingId) {
        String result = redisTemplate.opsForValue().get(generateKey(buskingId));
        return result == null ? 0L : Long.valueOf(result);
    }

    public void remove(Long buskingId) {
        redisTemplate.delete(generateKey(buskingId));
    }

    private String generateKey(Long buskingId) {
        return subscriptionCountKey.formatted(buskingId);
    }
}

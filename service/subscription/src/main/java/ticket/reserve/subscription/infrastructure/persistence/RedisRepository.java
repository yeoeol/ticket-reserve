package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.subscription.application.port.out.RedisPort;
import ticket.reserve.subscription.global.util.TimeConverterUtil;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RedisRepository implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.subscription-key:busking:subscription}")
    private String subscriptionKey;

    public void addToSubscriptionQueue(Long buskingId, Long userId, LocalDateTime startTime) {
        String value =  userId + ":" + buskingId;
        long startTimeMillis = TimeConverterUtil.convertToMilli(startTime);

        redisTemplate.opsForZSet().add(subscriptionKey, value, startTimeMillis);
    }

}

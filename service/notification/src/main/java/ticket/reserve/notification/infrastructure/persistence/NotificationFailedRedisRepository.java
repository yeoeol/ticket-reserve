package ticket.reserve.notification.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

@Repository
@RequiredArgsConstructor
public class NotificationFailedRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String FAIL_KEY = "notify:retry:%d";

    public void addToFailQueue(NotificationRetryDto retryDto) {
        String jsonValue = DataSerializer.serialize(retryDto);

        // Score: 현재 시간 + 300초
        long retryTimestamp = System.currentTimeMillis() / 1000 + 300;

        redisTemplate.opsForZSet().add(
                generateFailKey(retryDto.receiverId()),
                jsonValue,
                (double) retryTimestamp
        );
    }

    private String generateFailKey(Long id) {
        return FAIL_KEY.formatted(id);
    }
}

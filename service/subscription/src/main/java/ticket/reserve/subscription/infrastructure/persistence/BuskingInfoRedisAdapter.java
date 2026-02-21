package ticket.reserve.subscription.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.subscription.application.dto.response.BuskingNotificationTarget;
import ticket.reserve.subscription.application.port.out.BuskingInfoPort;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class BuskingInfoRedisAdapter implements BuskingInfoPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.busking-info-key:busking-info:%s}")
    private String buskingInfoKey;

    @Override
    public void createOrUpdate(Long buskingId, String title, String location, LocalDateTime startTime) {
        BuskingNotificationTarget target = new BuskingNotificationTarget(buskingId, title, location, startTime);
        String buskingNotificationTargetJson = DataSerializer.serialize(target);

        if (buskingNotificationTargetJson != null) {
            redisTemplate.opsForValue().set(generateKey(buskingId), buskingNotificationTargetJson);
        }
    }

    @Override
    public BuskingNotificationTarget read(Long buskingId) {
        String result = redisTemplate.opsForValue().get(generateKey(buskingId));
        if (result != null) {
            return DataSerializer.deserialize(result, BuskingNotificationTarget.class);
        } else {
            return null;
        }
    }

    private String generateKey(Long buskingId) {
        return buskingInfoKey.formatted(buskingId);
    }
}

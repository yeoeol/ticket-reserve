package ticket.reserve.notification.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Repository;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.port.out.RedisPort;

@Repository
@RequiredArgsConstructor
public class NotificationFailedRedisRepository implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String FAIL_KEY = "notify:retry:%d";

    @Override
    public void addToFailQueue(NotificationRetryDto retryDto, long delaySeconds) {
        String jsonValue = DataSerializer.serialize(retryDto);

        // Score: 현재 시간 + delaySeconds
        long retryTimestamp = System.currentTimeMillis() / 1000 + delaySeconds;

        redisTemplate.opsForZSet().add(
                generateFailKey(retryDto.receiverId()),
                jsonValue,
                (double) retryTimestamp
        );
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> search(Double buskingLng, Double buskingLat, double radiusKm) {
        return redisTemplate.opsForGeo().search(
                "user:locations",
                GeoReference.fromCoordinate(buskingLng, buskingLat),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );
    }

    @Override
    public boolean hasKey(Long userId) {
        return redisTemplate.hasKey("user:active:" + userId);
    }

    private String generateFailKey(Long id) {
        return FAIL_KEY.formatted(id);
    }
}

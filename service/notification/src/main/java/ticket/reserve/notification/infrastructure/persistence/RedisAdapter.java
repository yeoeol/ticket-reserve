package ticket.reserve.notification.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Repository;
import ticket.reserve.notification.application.port.out.RedisPort;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.geo-key:users:location}")
    private String geoKey;

    @Value("${app.redis.active-user-key:user:active}")
    private String activeUserKey;

    @Override
    public List<Long> findNearbyActiveUsers(Double buskingLng, Double buskingLat, double radiusKm) {
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = search(buskingLng, buskingLat, radiusKm);
        if (results == null) return List.of();

        return results.getContent().stream()
                .map(res -> Long.valueOf(res.getContent().getName()))
                .filter(this::hasActiveKey)
                .toList();
    }

    private GeoResults<RedisGeoCommands.GeoLocation<String>> search(Double buskingLng, Double buskingLat, double radiusKm) {
        return redisTemplate.opsForGeo().search(
                geoKey,
                GeoReference.fromCoordinate(buskingLng, buskingLat),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );
    }

    private boolean hasActiveKey(Long userId) {
        return redisTemplate.hasKey(generateActiveKey(userId));
    }

    private String generateActiveKey(Long userId) {
        return activeUserKey + ":" + userId;
    }
}

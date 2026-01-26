package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.port.out.RedisPort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisPort redisPort;

    public void addFailedNotification(NotificationRetryDto retryDto, long delaySeconds) {
        redisPort.addToFailQueue(retryDto, delaySeconds);
    }

    public List<Long> findNearbyActiveUsers(Double buskingLng, Double buskingLat, double radiusKm) {
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisPort.searchByGeo(buskingLng, buskingLat, radiusKm);

        if (results == null) return List.of();

        return results.getContent().stream()
                .map(res -> Long.valueOf(res.getContent().getName()))
                .filter(redisPort::hasKey)
                .toList();
    }
}

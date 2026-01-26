package ticket.reserve.notification.application.port.out;

import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

public interface RedisPort {
    void addToFailQueue(NotificationRetryDto retryDto, long delaySeconds);

    GeoResults<RedisGeoCommands.GeoLocation<String>> searchByGeo(Double buskingLng, Double buskingLat, double radiusKm);

    boolean hasKey(Long userId);
}

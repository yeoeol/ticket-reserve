package ticket.reserve.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.user.application.port.out.LocationPort;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LocationRedisAdapter implements LocationPort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.geo-key:users:location}")
    private String geoKey;

    @Value("${app.redis.active-user-key:user:active}")
    private String activeUserKey;

    @Override
    public void addLocation(Long userId, Double latitude, Double longitude) {
        String userIdString = String.valueOf(userId);

        redisTemplate.opsForGeo().add(geoKey, new Point(longitude, latitude), userIdString);
        redisTemplate.opsForValue().set(activeUserKey + ":" + userIdString, "active", 30, TimeUnit.MINUTES);
    }

    @Override
    public List<Long> findUserIds() {
        Set<String> userIds = redisTemplate.opsForZSet().range(geoKey, 0, -1);
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();

        return userIds.stream()
                .flatMap(userId -> {
                    try {
                        return Stream.of(Long.valueOf(userId));
                    } catch (NumberFormatException e) {
                        return Stream.empty();
                    }
                }).toList();

    }

    @Override
    public Map<Long, Point> getPointMapByUserIds(List<Long> userIds) {
        String[] userIdsArray = userIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        List<Point> points = redisTemplate.opsForGeo().position(geoKey, userIdsArray);
        if (points == null || points.isEmpty()) return Collections.emptyMap();

        Map<Long, Point> pointMap = new HashMap<>();

        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);
            Point point = points.get(i);

            if (point != null) {
                pointMap.put(userId, point);
            }
        }

        return pointMap;
    }
}

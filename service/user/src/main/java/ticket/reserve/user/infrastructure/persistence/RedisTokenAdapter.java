package ticket.reserve.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.user.application.port.out.TokenStorePort;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenAdapter implements TokenStorePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.geo-key:user:locations}")
    private String geoKey;

    @Override
    public void addBlackList(String token, long ttl) {
        redisTemplate.opsForValue().set("BL:"+token, "logout", ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addLocation(Long userId, Double latitude, Double longitude) {
        String userIdString = String.valueOf(userId);

        redisTemplate.opsForGeo().add(geoKey, new Point(longitude, latitude), userIdString);
        redisTemplate.opsForValue().set("user:active:" + userIdString, "active", 30, TimeUnit.MINUTES);
    }
}

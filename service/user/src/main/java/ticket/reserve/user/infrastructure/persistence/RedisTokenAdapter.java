package ticket.reserve.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.user.application.port.out.TokenStorePort;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenAdapter implements TokenStorePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addBlackList(String token, long ttl) {
        redisTemplate.opsForValue().set("BL:"+token, "logout", ttl, TimeUnit.MILLISECONDS);
    }
}

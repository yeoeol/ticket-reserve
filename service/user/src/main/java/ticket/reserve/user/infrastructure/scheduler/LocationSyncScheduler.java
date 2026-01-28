package ticket.reserve.user.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.BulkUserRepository;
import ticket.reserve.user.domain.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSyncScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final BulkUserRepository bulkUserRepository;

    @Value("${app.redis.geo-key:user:location}")
    private String geoKey;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void syncLocationFromRedisToDB() {
        log.info("[LocationSyncScheduler.syncLocationFromRedisToDB] Redis 위치 데이터 MySQL 백업 시작");

        Set<String> userIds = redisTemplate.opsForZSet().range(geoKey, 0, -1);
        if (userIds == null || userIds.isEmpty()) return;

        List<Long> userIdList = userIds.stream()
                .map(Long::valueOf)
                .toList();

        List<User> users = userRepository.findAllById(userIdList);
        Map<Long, Point> pointMap = new HashMap<>();

        for (String userIdStr : userIds) {
            List<Point> positions = redisTemplate.opsForGeo().position(geoKey, userIdStr);

            if (positions != null && !positions.isEmpty()) {
                Point pos = positions.getFirst();
                Long userId = Long.valueOf(userIdStr);

                pointMap.put(userId, pos);
            }
        }

        int updateCount = bulkUserRepository.LocationBulkUpdate(users, pointMap);
        log.info("[LocationSyncScheduler.syncLocationFromRedisToDB] 백업 완료: 총 {}명의 위치 정보 동기화", updateCount);
    }
}

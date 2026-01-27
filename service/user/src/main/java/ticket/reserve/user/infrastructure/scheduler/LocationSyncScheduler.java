package ticket.reserve.user.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSyncScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

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

        Map<Long, User> userMap = userRepository.findAllById(userIdList).stream()
                .collect(Collectors.toMap(
                        User::getId, Function.identity())
                );

        AtomicInteger updateCount = new AtomicInteger();
        for (String userIdStr : userIds) {
            List<Point> positions = redisTemplate.opsForGeo().position(geoKey, userIdStr);

            if (positions != null && !positions.isEmpty()) {
                Point pos = positions.getFirst();
                Long userId = Long.valueOf(userIdStr);

                User user = userMap.get(userId);
                if (user != null) {
                    org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                            new Coordinate(pos.getX(), pos.getY())
                    );
                    user.updateLocation(point);
                    updateCount.incrementAndGet();
                }
            }
        }
        log.info("[LocationSyncScheduler.syncLocationFromRedisToDB] 백업 완료: 총 {}명의 위치 정보 동기화", updateCount.get());
    }
}

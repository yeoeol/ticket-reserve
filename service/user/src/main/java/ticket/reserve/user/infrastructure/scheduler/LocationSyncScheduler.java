package ticket.reserve.user.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.user.application.port.out.LocationPort;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.BulkUserRepository;
import ticket.reserve.user.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSyncScheduler {

    private final UserRepository userRepository;
    private final BulkUserRepository bulkUserRepository;
    private final LocationPort locationPort;

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void syncLocationFromRedisToDB() {
        log.info("[LocationSyncScheduler.syncLocationFromRedisToDB] Redis 위치 데이터 MySQL 백업 시작");

        Set<String> userIds = locationPort.findUserIds();
        if (userIds == null || userIds.isEmpty()) return;

        List<Long> userIdList = userIds.stream()
                .map(Long::valueOf)
                .toList();

        Map<Long, Point> pointMap = locationPort.getPointMapByUserIds(userIdList);

        List<User> targetUsers = userRepository.findAllById(userIdList).stream()
                .filter(user -> pointMap.containsKey(user.getId()))
                .toList();

        int updateCount = bulkUserRepository.locationBulkUpdate(targetUsers, pointMap);
        log.info("[LocationSyncScheduler.syncLocationFromRedisToDB] 백업 완료: 총 {}명의 위치 정보 동기화", updateCount);
    }
}

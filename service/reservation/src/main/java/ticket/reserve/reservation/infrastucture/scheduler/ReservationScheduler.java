package ticket.reserve.reservation.infrastucture.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.reservation.application.ReservationExpiryService;

import java.util.Set;

import static java.util.concurrent.TimeUnit.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationExpiryService reservationExpiryService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long ALLOW_COUNT = 100L;   // 한 번에 입장시킬 인원 수 (조절 가능)
    private static final long ACTIVE_TTL = 5*60*1000L;  // ACTIVE 상태 유지 시간 (5분 뒤 만료)

    // 1분마다 실행
    @Scheduled(fixedDelay = 1L, timeUnit = MINUTES)
    public void triggerExpiredReservation() {
        reservationExpiryService.findAndPublishExpiredReservations();
    }

    // 3초마다 실행
    @Scheduled(fixedDelay = 3L, timeUnit = SECONDS)
    public void enterUser() {
        Long eventId = 1L; // 추후 수정

        String waitingKey = "queue:waiting:" + eventId;
        String activeKey = "queue:active:" + eventId;

        // 대기열에서 선착순 ALLOW_COUNT 수만큼 조회
        Set<String> usersToEnter = redisTemplate.opsForZSet().range(waitingKey, 0, ALLOW_COUNT - 1);
        if (usersToEnter == null || usersToEnter.isEmpty()) return;

        log.info("[ReservationScheduler.enterUser] {}명 입장 처리 중...", usersToEnter.size());

        // 입장열로 이동
        long expireTime = System.currentTimeMillis() + ACTIVE_TTL;
        for (String userId : usersToEnter) {
            redisTemplate.opsForZSet().add(activeKey, userId, expireTime);
            redisTemplate.opsForZSet().remove(waitingKey, userId);
        }
    }
}

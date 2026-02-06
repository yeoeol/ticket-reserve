package ticket.reserve.busking.application.port.out;

import java.time.LocalDateTime;

public interface RedisPort {
    boolean isSubscribed(Long buskingId, Long userId);
    void addToNotificationSchedule(Long buskingId, LocalDateTime startTime);
}

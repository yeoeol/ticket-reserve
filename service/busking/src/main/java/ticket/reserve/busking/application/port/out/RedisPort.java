package ticket.reserve.busking.application.port.out;

import java.time.LocalDateTime;

public interface RedisPort {
    void addToNotificationSchedule(Long buskingId, LocalDateTime startTime, LocalDateTime endTime);
}

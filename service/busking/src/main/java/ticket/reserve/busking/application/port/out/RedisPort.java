package ticket.reserve.busking.application.port.out;

import java.time.LocalDateTime;

public interface RedisPort {
    void addToNotificationSchedule(Long buskingId, double lat, double lng, LocalDateTime startTime, LocalDateTime endTime);
}

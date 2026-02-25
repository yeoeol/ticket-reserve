package ticket.reserve.busking.application.port.out;

import java.time.LocalDateTime;

public interface NotificationSchedulePort {
    void addToNotificationSchedule(Long buskingId, LocalDateTime startTime, LocalDateTime endTime);

    void removeToNotificationSchedule(Long buskingId);
}

package ticket.reserve.subscription.application.port.out;

import java.time.LocalDateTime;

public interface RedisPort {
    void addToSubscriptionQueue(Long buskingId, Long userId, LocalDateTime startTime);

    void addToNotificationSchedule(Long buskingId, LocalDateTime startTime);

    void removeFromSubscriptionQueue(Long buskingId, Long userId);
}

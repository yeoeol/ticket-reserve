package ticket.reserve.notification.application.port.out;

import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

public interface RedisPort {
    void addToFailQueue(NotificationRetryDto retryDto, long delaySeconds);
}

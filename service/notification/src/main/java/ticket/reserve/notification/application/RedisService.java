package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.port.out.RedisPort;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisPort redisPort;

    public void addFailedNotification(NotificationRetryDto retryDto, long delaySeconds) {
        redisPort.addToFailQueue(retryDto, delaySeconds);
    }
}

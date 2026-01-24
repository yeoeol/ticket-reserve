package ticket.reserve.notification.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.domain.Notification;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;

    private static final String FAIL_KEY = "notify:retry:*";

    @Scheduled(initialDelay = 10, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void retryFailedNotifications() {
        long now = System.currentTimeMillis() / 1000;

        Set<String> keys = redisTemplate.keys(FAIL_KEY);

        for (String key : keys) {
            Set<String> failedNotifcations = redisTemplate.opsForZSet().rangeByScore(key, 0, now);

            for (String failedNotificationJson : failedNotifcations) {
                Notification notification = DataSerializer.deserialize(failedNotificationJson, Notification.class);

                if (notification != null) {
                    notificationService.createAndSend(NotificationRequestDto.from(notification));
                    redisTemplate.opsForZSet().remove(key, failedNotificationJson);
                }
            }
        }
    }
}

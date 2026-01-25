package ticket.reserve.notification.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;

    private static final String FAIL_KEY_PATTERN = "notify:retry:*";

    @Scheduled(initialDelay = 10, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void retryFailedNotifications() {
        long now = System.currentTimeMillis() / 1000;

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(FAIL_KEY_PATTERN)
                .count(10)
                .build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .keyCommands()
                .scan(scanOptions)
        ) {
           while (cursor.hasNext()) {
               String key = new String(cursor.next());
               Set<String> failedNotifications = redisTemplate.opsForZSet().rangeByScore(key, 0, now);

               for (String failedNotificationJson : failedNotifications) {
                   NotificationRetryDto retryDto = DataSerializer.deserialize(failedNotificationJson, NotificationRetryDto.class);

                   if (retryDto != null) {
                       notificationService.createAndSend(NotificationRequestDto.from(retryDto));
                       redisTemplate.opsForZSet().remove(key, failedNotificationJson);
                   }
               }
           }
        }
    }
}

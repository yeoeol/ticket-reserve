package ticket.reserve.notification.infrastructure.scheduler;

import org.springframework.beans.factory.annotation.Value;
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
public class NotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;
    private final String FAIL_KEY_PATTERN;

    public NotificationScheduler(
            RedisTemplate<String, String> redisTemplate,
            NotificationService notificationService,
            @Value("${app.redis.fail-key:notify:retry}") String failKey
    ) {
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
        this.FAIL_KEY_PATTERN = failKey + ":*";
    }


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

               if (failedNotifications == null || failedNotifications.isEmpty()) {
                   continue;
               }

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

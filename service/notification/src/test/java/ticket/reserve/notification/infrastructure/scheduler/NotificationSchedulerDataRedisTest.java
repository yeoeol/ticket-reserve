package ticket.reserve.notification.infrastructure.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataRedisTest
@Import({NotificationScheduler.class, DataSerializer.class})
class NotificationSchedulerDataRedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private NotificationScheduler notificationScheduler;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    @DisplayName("SCAN을 통해 패턴에 매칭되는 여러 개의 키를 모두 찾아내야 한다")
    void key_scan_success() {
        //given
        String matchKey1 = "notify:retry:123";
        String matchKey2 = "notify:retry:456";
        String matchKey3 = "notify:retry:789";
        String otherKey = "other:data:123";

        NotificationRetryDto retryDto =
                createNotificationRetry("제목", "내용", 1234L, 1L, 0);
        NotificationRetryDto retryDto2 =
                createNotificationRetry("제목2", "내용2", 1234L, 1L, 0);
        NotificationRetryDto retryDto3 =
                createNotificationRetry("제목3", "내용3", 1234L, 1L, 0);

        long now = System.currentTimeMillis() / 1000;
        redisTemplate.opsForZSet().add(matchKey1, DataSerializer.serialize(retryDto), now - 100);
        redisTemplate.opsForZSet().add(matchKey2, DataSerializer.serialize(retryDto2), now - 100);
        redisTemplate.opsForZSet().add(matchKey3, DataSerializer.serialize(retryDto3), now - 100);
        redisTemplate.opsForValue().set(otherKey, "json4");

        //when
        notificationScheduler.retryFailedNotifications();

        //then
        verify(notificationService, times(3)).createAndSend(any());
    }

    private static NotificationRetryDto createNotificationRetry(
            String title, String message, Long receiverId, Long buskingId, int retryCount
    ) {
        return new NotificationRetryDto(
                title, message, receiverId, buskingId, retryCount
        );
    }
}
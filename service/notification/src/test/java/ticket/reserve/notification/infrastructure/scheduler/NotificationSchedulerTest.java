package ticket.reserve.notification.infrastructure.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import ticket.reserve.core.dataserializer.DataSerializer;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.domain.Notification;
import ticket.reserve.notification.domain.repository.NotificationRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NotificationSchedulerTest {

    @Autowired
    private NotificationScheduler notificationScheduler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final String FAIL_KEY = "notify:retry:%d";

    @BeforeEach
    void setUp() {
        // Redis 특정 키 삭제
        redisTemplate.delete(FAIL_KEY.formatted(1234L));
        // DB 초기화
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("재알림 스케줄링 실행 성공 시 Redis에서 데이터가 삭제되고 DB에 저장된다")
    void test() {
        //given
        NotificationRetryDto retryDto =
                createNotificationRetry("제목", "내용", 1234L, 1L);
        redisTemplate.opsForZSet().add(
                FAIL_KEY.formatted(1234L),
                DataSerializer.serialize(retryDto),
                (double) System.currentTimeMillis() /1000-300
        );
        NotificationRetryDto retryDto2 =
                createNotificationRetry("제목2", "내용2", 1234L, 1L);
        redisTemplate.opsForZSet().add(
                FAIL_KEY.formatted(1234L),
                DataSerializer.serialize(retryDto2),
                (double) System.currentTimeMillis() /1000-400
        );

        //when
        notificationScheduler.retryFailedNotifications();

        //then
        List<Notification> notificationList = notificationRepository.findByReceiverId(1234L);
        Long count = redisTemplate.opsForZSet().count(
                FAIL_KEY.formatted(1234L),
                0,
                (double) System.currentTimeMillis() / 1000
        );
        assertThat(count).isEqualTo(0);
        assertThat(notificationList).hasSize(2);
        assertThat(notificationList.getFirst().getReceiverId()).isEqualTo(1234L);
    }

    private static NotificationRetryDto createNotificationRetry(String title, String message, Long receiverId, Long buskingId) {
        return new NotificationRetryDto(
                title, message, receiverId, buskingId
        );
    }
}
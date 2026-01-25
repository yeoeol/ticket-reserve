package ticket.reserve.notification.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NotificationFailedRedisRepositoryTest {

    @Autowired
    private NotificationFailedRedisRepository redisRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("실패한 알림 정보가 Redis ZSet에 JSON 형태로 저장된다")
    void redis_zset_save_test() {
        redisTemplate.delete("notify:retry:1234");

        //given
        NotificationRetryDto retryDto = new NotificationRetryDto(
                "제목", "내용", 1234L, 1L, 0
        );

        //when
        redisRepository.addToFailQueue(retryDto, 300);

        //then
        Set<String> results = redisTemplate.opsForZSet().range("notify:retry:1234", 0, -1);
        assertThat(results).anyMatch(json -> json.contains("\"buskingId\":1"));
    }

}
package ticket.reserve.notification.infrastructure.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.test.context.ActiveProfiles;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NotificationFailedRedisRepositoryTest {

    @Autowired
    private NotificationFailedRedisRepository redisRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.fail-key:notify:retry}")
    private String failKey;

    @Value("${app.redis.geo-key:user:locations}")
    private String geoKey;

    @Value("${app.redis.active-user-key:user:active}")
    private String activeUserKey;

    @AfterEach
    void after() {
        Set<String> keys = redisTemplate.keys(failKey+":*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        Set<String> keys2 = redisTemplate.keys(activeUserKey+":*");
        if (!keys2.isEmpty()) {
            redisTemplate.delete(keys2);
        }
        redisTemplate.delete(geoKey);
    }


    @Test
    @DisplayName("실패한 알림 정보가 Redis ZSet에 JSON 형태로 저장된다")
    void redis_zset_save_test() {
        //given
        NotificationRetryDto retryDto = new NotificationRetryDto(
                "제목", "내용", 1234L, 1L, 0
        );

        //when
        redisRepository.addToFailQueue(retryDto, 300);

        //then
        Set<String> results = redisTemplate.opsForZSet().range(failKey + ":1234", 0, -1);
        assertThat(results).anyMatch(json -> json.contains("\"buskingId\":1"));
    }

    @Test
    @DisplayName("RedisTemplate - 특정 좌표 기준으로 반경 radiusKm 이내를 조회한다")
    void find_within_radiusKm_by_specific_coordinate_criteria() {
        //given
        redisTemplate.opsForGeo().add(geoKey, new Point(0.01, 0.01), "1"); // 약 1.5km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.02, 0.02), "2"); // 약 3.1km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.03, 0.03), "3"); // 약 4.7km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.1, 0.1), "4");   // 약 15km (제외될 것)

        //when
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .includeDistance()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(
                geoKey,
                GeoReference.fromCoordinate(0.0, 0.0),
                new Distance(5.0, Metrics.KILOMETERS),
                args
        );

        List<String> userIds = new ArrayList<>();
        results.getContent().forEach(result -> {
            String userId = result.getContent().getName();
            System.out.println("발견된 유저 ID: " + userId +
                    ", 거리: " + result.getDistance().getValue() + "km");
            userIds.add(userId);
        });

        //then
        assertThat(userIds).hasSize(3);
        assertThat(userIds).containsExactly("1", "2", "3");
    }

    @Test
    @DisplayName("RedisRepository - 특정 좌표 기준으로 반경 radiusKm 이내를 조회한다")
    void find_within_radiusKm_by_specific_coordinate_criteria2() {
        //given
        redisTemplate.opsForGeo().add(geoKey, new Point(0.01, 0.01), "1"); // 약 1.5km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.02, 0.02), "2"); // 약 3.1km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.03, 0.03), "3"); // 약 4.7km
        redisTemplate.opsForGeo().add(geoKey, new Point(0.1, 0.1), "4");   // 약 15km (제외될 것)

        //when
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisRepository.search(
                0.0, 0.0, 5
        );

        List<String> userIds = new ArrayList<>();
        results.getContent().forEach(result -> {
            String userId = result.getContent().getName();
            System.out.println("발견된 유저 ID: " + userId);
            userIds.add(userId);
        });

        //then
        assertThat(userIds).hasSize(3);
        assertThat(userIds).containsExactly("1", "2", "3");
    }

}
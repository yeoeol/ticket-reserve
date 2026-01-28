package ticket.reserve.user.infrastructure.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ticket.reserve.user.domain.user.repository.BulkUserRepository;
import ticket.reserve.user.domain.user.repository.UserRepository;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DataRedisTest
@Import({LocationSyncScheduler.class})
@ActiveProfiles("test")
class LocationSyncSchedulerDataRedisTest {

    @Autowired
    private LocationSyncScheduler locationSyncScheduler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BulkUserRepository bulkUserRepository;


    @Value("${app.redis.geo-key:user:locations}")
    private String geoKey;

    @BeforeEach
    void setUp() {
        redisTemplate.delete(geoKey);
    }

    @AfterEach
    void after() {
        redisTemplate.delete(geoKey);
    }

    @Test
    @DisplayName("Redis에 GEO_KEY로 저장되어 있는 사용자ID를 조회한다")
    void find_userIds_by_geoKey_in_redis() {
        //given
        redisTemplate.opsForGeo().add(geoKey, new Point(100, 50), "1");
        redisTemplate.opsForGeo().add(geoKey, new Point(110, 60), "2");
        redisTemplate.opsForGeo().add(geoKey, new Point(120, 70), "3");

        //when
        locationSyncScheduler.syncLocationFromRedisToDB();

        //then
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).findAllById(captor.capture());

        assertThat(captor.getValue()).hasSize(3);
        assertThat(captor.getValue()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }
}
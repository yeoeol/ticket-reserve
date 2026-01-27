package ticket.reserve.user.infrastructure.scheduler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ticket.reserve.user.domain.user.repository.UserRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataRedisTest
@Import({LocationSyncScheduler.class})
@ActiveProfiles("test")
class LocationSyncSchedulerTest {

    @Autowired
    private LocationSyncScheduler locationSyncScheduler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockitoBean
    private UserRepository userRepository;

    private static final String GEO_KEY = "user:location:test";

    @BeforeEach
    void setUp() {
        redisTemplate.delete(GEO_KEY);
    }

    @AfterEach
    void after() {
        redisTemplate.delete(GEO_KEY);
    }

    @Test
    @DisplayName("Redis에 GEO_KEY로 저장되어 있는 사용자ID를 조회한다")
    void find_userIds_by_geoKey_in_redis() {
        //given
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(100, 50), "1");

        //when
        locationSyncScheduler.syncLocationFromRedisToDB();

        //then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(captor.capture());

        Assertions.assertThat(captor.getValue()).isEqualTo(1L);
    }
}
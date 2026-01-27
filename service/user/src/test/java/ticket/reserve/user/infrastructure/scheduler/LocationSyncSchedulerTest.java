package ticket.reserve.user.infrastructure.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.infrastructure.persistence.RedisTokenAdapter;

@SpringBootTest
class LocationSyncSchedulerTest {

    @Autowired
    private LocationSyncScheduler locationSyncScheduler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTokenAdapter redisTokenAdapter;

    @Value("${app.redis.geo-key:user:locations}")
    private String geoKey;

    @Test
    void verify_query_count() {
        for (int i = 1; i <= 5; i++) {
            User user = createUser("testusername" + i, "testpassword" + i, "test" + i + "@gmail.com");
            userRepository.save(user);
            redisTokenAdapter.addLocation(user.getId(), 37.0, 127.0);
        }

        System.out.println("========================================");
        locationSyncScheduler.syncLocationFromRedisToDB();
    }

    private User createUser(String username, String rawPassword, String email) {
        return User.create(
                idGenerator,
                username,
                passwordEncoder.encode(rawPassword),
                email
        );
    }
}
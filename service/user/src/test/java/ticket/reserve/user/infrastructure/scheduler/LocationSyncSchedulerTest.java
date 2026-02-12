package ticket.reserve.user.infrastructure.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.infrastructure.persistence.TokenRedisAdapter;

@SpringBootTest
class LocationSyncSchedulerTest {

    @Autowired
    private LocationSyncScheduler locationSyncScheduler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRedisAdapter tokenRedisAdapter;

    @Test
    void verify_query_count() {
        for (int i = 1; i <= 5; i++) {
            User user = createUser("testusername" + i, "testpassword" + i, "test" + i + "@gmail.com");
            userRepository.save(user);
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
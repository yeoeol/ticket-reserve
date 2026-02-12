package ticket.reserve.user.infrastructure.scheduler;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.user.application.port.out.LocationPort;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LocationSyncSchedulerTest {

    @Autowired
    private LocationSyncScheduler locationSyncScheduler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private LocationPort locationPort;

    @Test
    void verify_query_count() {
        //given
        for (int i = 1; i <= 5; i++) {
            User user = createUser("testusername" + i, "testpassword" + i, "test" + i + "@gmail.com");
            userRepository.save(user);
            locationPort.addLocation(user.getId(), 37.5, 127.5);
        }

        //when
        locationSyncScheduler.syncLocationFromRedisToDB();

        //then
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(5);
        for (User user : users) {
            assertThat(user.getLocation().getY()).isEqualTo(37.5, Offset.offset(0.001));
            assertThat(user.getLocation().getX()).isEqualTo(127.5, Offset.offset(0.001));
        }
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
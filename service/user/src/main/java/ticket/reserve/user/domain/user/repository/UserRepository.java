package ticket.reserve.user.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}

package ticket.reserve.user.domain.userrole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(User user, Role role);
}

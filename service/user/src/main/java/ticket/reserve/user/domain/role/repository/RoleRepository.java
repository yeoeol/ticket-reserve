package ticket.reserve.user.domain.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.role.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}

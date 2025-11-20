package ticket.reserve.user.domain.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.role.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

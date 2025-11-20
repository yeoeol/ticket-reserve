package ticket.reserve.user.domain.userrole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.userrole.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}

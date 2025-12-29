package ticket.reserve.user.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.user.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    /**
     * N+1 제거를 위한 페치 조인 쿼리 -> yml의 Batch Size 설정을 통해 IN절을 사용하는 것으로 대체
     *
    @Query("SELECT u " +
            "FROM User u join fetch u.userRoles ur join fetch ur.role r " +
            "WHERE u.username=:username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u " +
           "FROM User u " +
           "   JOIN FETCH u.userRoles ur " +
           "   JOIN FETCH ur.role r")
    List<User> findAllWithRoles();
     */
}

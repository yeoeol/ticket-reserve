package ticket.reserve.user.domain.user;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.tsid.IdGenerator;
import ticket.reserve.user.domain.BaseTimeEntity;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Builder(access = AccessLevel.PRIVATE)
    private User(IdGenerator idGenerator, String username, String password, String email) {
        this.id = idGenerator.nextId();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static User create(IdGenerator idGenerator, String username, String encodedPassword, String email) {
        return User.builder()
                .idGenerator(idGenerator)
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();
    }

    public void update(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void addRole(IdGenerator idGenerator, Role role) {
        UserRole userRole = UserRole.create(
                idGenerator,
                this,
                role
        );
        this.userRoles.add(userRole);
    }

    public List<String> getRoleNames() {
        return this.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();
    }
}

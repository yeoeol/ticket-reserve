package ticket.reserve.user.domain.user;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.user.domain.BaseTimeEntity;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    public static User of(String username, String encodedPassword, String email) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();
    }

    public void update(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void addRole(Role role) {
        UserRole userRole = UserRole.builder()
                .user(this)
                .role(role)
                .build();
        this.userRoles.add(userRole);
    }
}

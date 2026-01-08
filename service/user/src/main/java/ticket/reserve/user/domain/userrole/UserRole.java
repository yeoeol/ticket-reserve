package ticket.reserve.user.domain.userrole;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.tsid.IdGenerator;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_role")
public class UserRole {

    @Id
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    private UserRole(IdGenerator idGenerator, User user, Role role) {
        this.id = idGenerator.nextId();
        this.user = user;
        this.role = role;
    }
}

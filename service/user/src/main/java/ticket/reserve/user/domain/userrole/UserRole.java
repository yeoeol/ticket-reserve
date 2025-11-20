package ticket.reserve.user.domain.userrole;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.user.User;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_role")
public class UserRole {

    @Id @GeneratedValue
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}

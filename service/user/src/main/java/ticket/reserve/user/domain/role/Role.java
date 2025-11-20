package ticket.reserve.user.domain.role;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "roles")
public class Role {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    private Set<UserRole> users = new HashSet<>();
}

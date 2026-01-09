package ticket.reserve.user.domain.role;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @Builder(access = AccessLevel.PRIVATE)
    private Role(IdGenerator idGenerator, String roleName, String roleDesc) {
        this.id = idGenerator.nextId();
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    public static Role create(IdGenerator idGenerator, String roleName, String roleDesc) {
        return Role.builder()
                .idGenerator(idGenerator)
                .roleName(roleName)
                .roleDesc(roleDesc)
                .build();
    }
}

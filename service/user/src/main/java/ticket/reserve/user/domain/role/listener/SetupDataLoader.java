package ticket.reserve.user.domain.role.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.role.repository.RoleRepository;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.domain.userrole.UserRole;
import ticket.reserve.user.domain.userrole.repository.UserRoleRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean alreadySetup = false;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        setUpData();
        alreadySetup = true;
    }

    private void setUpData() {
        User admin = createUserIfNotFound("admin", "admin@gmail.com", "1111");
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        UserRole userRole = createUserRoleIfNotFound(admin, adminRole);
        admin.getUserRoles().add(userRole);
    }

    private User createUserIfNotFound(String username, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .build();
            return userRepository.save(user);
        }
        return optionalUser.get();
    }

    private Role createRoleIfNotFound(String roleName, String roleDesc) {
        Optional<Role> optionalRole = roleRepository.findByRoleName(roleName);
        if (optionalRole.isEmpty()) {
            Role role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
            return roleRepository.save(role);
        }
        return optionalRole.get();
    }

    private UserRole createUserRoleIfNotFound(User user, Role role) {
        Optional<UserRole> optionalUserRole = userRoleRepository.findByUserAndRole(user, role);
        if (optionalUserRole.isEmpty()) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            return userRoleRepository.save(userRole);
        }
        return optionalUserRole.get();
    }
}

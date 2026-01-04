package ticket.reserve.user.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.port.out.GenerateTokenPort;
import ticket.reserve.user.application.port.out.TokenStorePort;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.role.repository.RoleRepository;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock GenerateTokenPort generateTokenPort;
    @Mock TokenStorePort tokenStorePort;

    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .roleName("ROLE_USER")
                .roleDesc("사용자")
                .build();
        user = User.builder()
                .id(1234L)
                .username("testusername")
                .password("encodedPassword")
                .email("test@naver.com")
                .build();
    }

    @Test
    @DisplayName("회원가입 - 성공 시 암호화된 비밀번호화 유저 정보를 저장한다")
    void registerTest() {
        //given
        UserRegisterRequestDto request = new UserRegisterRequestDto(
        "testusername", "rawPassword", "test@naver.com"
        );

        given(roleRepository.findByRoleName("ROLE_USER"))
                .willReturn(Optional.of(role));
        given(passwordEncoder.encode("rawPassword"))
                .willReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        given(userRepository.save(userCaptor.capture()))
                .willReturn(user);

        //when
        Long registerId = userService.register(request);

        //then
        User savedUser = userCaptor.getValue();

        assertThat(registerId).isEqualTo(1234L);
        assertThat(savedUser.getUsername()).isEqualTo("testusername");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getEmail()).isEqualTo("test@naver.com");

        assertThat(savedUser.getUserRoles()).hasSize(1);
        assertThat(savedUser.getUserRoles())
                .extracting(UserRole::getRole)
                .containsExactly(role)
                .extracting(Role::getRoleName)
                .containsExactly("ROLE_USER");

        verify(userRepository, times(1)).save(any());
    }

}
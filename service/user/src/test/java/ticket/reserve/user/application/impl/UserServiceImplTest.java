package ticket.reserve.user.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.core.tsid.TsidIdGenerator;
import ticket.reserve.user.application.UserService;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.dto.request.UserUpdateRequestDto;
import ticket.reserve.user.application.dto.response.UserLoginResponseDto;
import ticket.reserve.user.application.dto.response.UserResponseDto;
import ticket.reserve.user.application.port.out.GenerateTokenPort;
import ticket.reserve.user.application.port.out.LocationPort;
import ticket.reserve.user.application.port.out.TokenStorePort;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.role.repository.RoleRepository;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.domain.userrole.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock IdGenerator idGenerator;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock GenerateTokenPort generateTokenPort;
    @Mock LocationPort locationPort;
    @Mock TokenStorePort tokenStorePort;

    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        idGenerator = new TsidIdGenerator();
        role = Role.create(
                idGenerator,
                "ROLE_USER",
                "사용자"
        );
        user = User.create(
                idGenerator,
                "testusername",
                "encodedPassword",
                "test@naver.com"
        );
        user.addRole(idGenerator, role);
    }

    @Test
    @DisplayName("회원가입 성공 - 암호화된 비밀번호화 유저 정보를 저장한다")
    void register_Success() {
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

        assertThat(registerId).isEqualTo(user.getId());
        assertThat(savedUser.getUsername()).isEqualTo(request.username());
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getEmail()).isEqualTo(request.email());

        assertThat(savedUser.getUserRoles()).hasSize(1);
        assertThat(savedUser.getUserRoles())
                .extracting(UserRole::getRole)
                .containsExactly(role);
        assertThat(savedUser.getUserRoles())
                .extracting(UserRole::getRole)
                .extracting(Role::getRoleName)
                .containsExactly("ROLE_USER");

        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 정보 입력 시 JWT 토큰을 반환한다")
    void loginSuccess() {
        //given
        String username = "testusername";
        String password = "rawPassword";
        String expectedToken = "valid.jwt.token";

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword()))
                .willReturn(true);
        given(generateTokenPort.generateToken(user.getId(), user.getRoleNames()))
                .willReturn(expectedToken);

        //when
        UserLoginResponseDto responseDto = userService.login(username, password);
        String generatedToken = responseDto.accessToken();

        //then
        assertThat(generatedToken).isEqualTo(expectedToken);

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, user.getPassword());
        verify(generateTokenPort, times(1)).generateToken(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않으면 INVALID_LOGIN 예외가 발생한다")
    void loginFail_InvalidPassword() {
        //given
        String username = "testusername";
        String wrongPassword = "wrongPassword";

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(wrongPassword, user.getPassword()))
                .willReturn(false);

        //when
        Throwable throwable = catchThrowable(() -> userService.login(username, wrongPassword));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_LOGIN.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_LOGIN);
        verify(generateTokenPort, times(0)).generateToken(any(), any());
    }

    @Test
    @DisplayName("사용자 수정 성공 - 기존 사용자 정보가 파라미터로 넘어온 수정 정보로 변경된다")
    void updateUserSuccess() {
        //given
        UserUpdateRequestDto request = new UserUpdateRequestDto(
                user.getId(), "updateusername", "encodedPassword", "updateEmail@naver.com"
        );

        given(userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword()))
                .willReturn(true);

        //when
        UserResponseDto response = userService.updateUser(request);

        //then
        // 실제 User 엔티티 필드 변경 검증
        assertThat(user.getUsername()).isEqualTo(request.username());
        assertThat(user.getEmail()).isEqualTo(request.email());

        // 응답 DTO 검증
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.email()).isEqualTo(request.email());
    }
}
package ticket.reserve.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.user.application.port.out.GenerateTokenPort;
import ticket.reserve.user.domain.role.Role;
import ticket.reserve.user.domain.role.repository.RoleRepository;
import ticket.reserve.user.domain.user.User;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.dto.response.UserResponseDto;
import ticket.reserve.user.application.dto.request.UserUpdateRequestDto;
import ticket.reserve.user.domain.user.repository.UserRepository;
import ticket.reserve.user.domain.userrole.UserRole;
import ticket.reserve.user.domain.userrole.repository.UserRoleRepository;
import ticket.reserve.user.infrastructure.security.TokenAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenerateTokenPort generateTokenPort;
    private final TokenAdapter tokenAdapter;

    @Transactional
    public Long register(UserRegisterRequestDto requestDto) {
        Role roleUser = roleRepository.findByRoleName("ROLE_USER").get();
        User user = User.builder()
                .username(requestDto.username())
                .password(passwordEncoder.encode(requestDto.password()))
                .email(requestDto.email())
                .build();

        UserRole userRole = userRoleRepository.save(
                UserRole.builder()
                    .user(user)
                    .role((roleUser))
                    .build()
        );
        user.getUserRoles().add(userRole);

        return userRepository.save(user).getId();
    }

    @Transactional
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_LOGIN);
        }
        List<String> userRoles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();

        return generateTokenPort.generateToken(user.getId(), userRoles);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponseDto::from)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validatePassword(request.password(), user.getPassword());
        user.update(request.username(), request.email());

        return UserResponseDto.from(user);
    }

    public void logout(String accessToken) {
        if (accessToken != null) {
            long remainingTime = tokenAdapter.getRemainingTime(accessToken);

            if (remainingTime > 0) {
                redisService.save("BL:"+accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS);
            }
        }

    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.INVALID_LOGIN);
        }
    }
}

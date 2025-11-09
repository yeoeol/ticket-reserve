package ticket.reserve.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.user.domain.User;
import ticket.reserve.user.dto.UserRegisterRequestDto;
import ticket.reserve.user.dto.UserResponseDto;
import ticket.reserve.user.dto.UserUpdateRequestDto;
import ticket.reserve.user.repository.UserRepository;
import ticket.reserve.user.util.JwtUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Long register(UserRegisterRequestDto requestDto) {
        User user = User.builder()
                .username(requestDto.username())
                .password(passwordEncoder.encode(requestDto.password()))
                .email(requestDto.email())
                .build();

        return userRepository.save(user).getId();
    }

    @Transactional
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀립니다.");
        }
        user.update(request.username(), request.email());

        return UserResponseDto.from(user);
    }
}

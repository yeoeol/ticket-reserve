package ticket.reserve.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ticket.reserve.user.domain.User;
import ticket.reserve.user.dto.UserRegisterRequestDto;
import ticket.reserve.user.repository.UserRepository;
import ticket.reserve.user.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Long register(UserRegisterRequestDto requestDto) {
        User user = User.builder()
                .username(requestDto.username())
                .password(passwordEncoder.encode(requestDto.password()))
                .email(requestDto.email())
                .build();

        return userRepository.save(user).getId();
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getId());
    }
}

package ticket.reserve.user.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.application.dto.request.UserLoginRequestDto;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.dto.response.UserLoginResponseDto;
import ticket.reserve.user.application.dto.response.UserResponseDto;
import ticket.reserve.user.application.dto.request.UserUpdateRequestDto;
import ticket.reserve.user.application.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody UserRegisterRequestDto requestDto) {
        Long userId = userService.register(requestDto);

        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto requestDto
    ) {
        return ResponseEntity.ok(userService.login(requestDto.username(), requestDto.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        userService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }
}

package ticket.reserve.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.dto.UserLoginRequestDto;
import ticket.reserve.user.dto.UserRegisterRequestDto;
import ticket.reserve.user.dto.UserResponseDto;
import ticket.reserve.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/users/register")
    public ResponseEntity<Map<String, Long>> register(@RequestBody UserRegisterRequestDto requestDto) {
        Long userId = userService.register(requestDto);

        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto.username(), requestDto.password());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }
}

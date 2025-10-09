package ticket.reserve.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.dto.UserLoginRequestDto;
import ticket.reserve.user.dto.UserRegisterRequestDto;
import ticket.reserve.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "user-service";
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@RequestBody UserRegisterRequestDto requestDto) {
        Long userId = userService.register(requestDto);

        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto.username(), requestDto.password());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{userId}")
    public String get(@PathVariable Long userId) {
        System.out.println("[UserController.get]");
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

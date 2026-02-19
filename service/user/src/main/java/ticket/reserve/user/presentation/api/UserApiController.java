package ticket.reserve.user.presentation.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.application.dto.request.LocationRequestDto;
import ticket.reserve.user.application.dto.request.UserLoginRequestDto;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.dto.response.UserLoginResponseDto;
import ticket.reserve.user.application.dto.response.UserResponseDto;
import ticket.reserve.user.application.dto.request.UserUpdateRequestDto;
import ticket.reserve.user.application.UserService;
import ticket.reserve.user.global.util.CookieUtil;

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
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        UserLoginResponseDto loginResponseDto = userService.login(requestDto.username(), requestDto.password());

        Cookie accessTokenCookie = CookieUtil.setHttpOnlyCookie(
                "accessToken", loginResponseDto.accessToken(), 60 * 60 * 24 // 1일
        );
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(loginResponseDto);
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

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @Valid @RequestBody LocationRequestDto request,
            @AuthenticationPrincipal String userId
    ) {
        userService.updateLocation(Long.valueOf(userId), request.latitude(), request.longitude());
        return ResponseEntity.noContent().build();
    }
}

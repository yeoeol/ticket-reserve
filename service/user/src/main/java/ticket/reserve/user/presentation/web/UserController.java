package ticket.reserve.user.presentation.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.application.dto.request.UserLoginRequestDto;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(UserRegisterRequestDto requestDto) {
        userService.register(requestDto);
        return "redirect:/users/register";
    }

    @PostMapping("/login")
    public String login(UserLoginRequestDto requestDto, HttpServletResponse response) {
        String token = userService.login(requestDto.username(), requestDto.password());
        Cookie accessTokenCookie = setHttpOnlyCookie("accessToken", token, 60 * 60 * 24);// 1일

        response.addCookie(accessTokenCookie);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {
        userService.logout(accessToken);

        Cookie accessTokenCookie = setHttpOnlyCookie("accessToken", null, 0);
        response.addCookie(accessTokenCookie);
        return "redirect:/";
    }

    private Cookie setHttpOnlyCookie(String key, String value, int expiry) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiry);
        return cookie;
    }
}

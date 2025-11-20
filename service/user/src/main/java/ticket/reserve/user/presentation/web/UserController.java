package ticket.reserve.user.presentation.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
        setHttpOnlyCookie(token, response);

        response.addHeader(HttpHeaders.AUTHORIZATION, token);
        return "redirect:/";
    }

    private static void setHttpOnlyCookie(String accessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60*60*24);  // 1일
        response.addCookie(accessTokenCookie);
    }
}

package ticket.reserve.user.presentation.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.application.dto.request.UserLoginRequestDto;
import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.UserService;
import ticket.reserve.user.application.dto.response.UserLoginResponseDto;
import ticket.reserve.user.global.util.CookieUtil;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            Model model
    ) {
        model.addAttribute("redirectUri", redirectUri);
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid UserRegisterRequestDto requestDto,
            @RequestParam(value = "redirectUri", required = false) String redirectUri
    ) {
        userService.register(requestDto);

        if (StringUtils.hasText(redirectUri)) {
            return "redirect:/users/register?redirectUri=" + redirectUri;
        }
        return "redirect:/users/register";
    }

    @PostMapping("/login")
    public String login(
            @Valid UserLoginRequestDto requestDto,
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            HttpServletResponse response
    ) {
        UserLoginResponseDto loginResponseDto = userService.login(requestDto.username(), requestDto.password());

        Cookie accessTokenCookie = CookieUtil.setHttpOnlyCookie("accessToken", loginResponseDto.accessToken(), 60 * 60 * 24);// 1일
        response.addCookie(accessTokenCookie);

        if (StringUtils.hasText(redirectUri)) {
            return "redirect:" + redirectUri;
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {
        userService.logout(accessToken);

        Cookie accessTokenCookie = CookieUtil.setHttpOnlyCookie("accessToken", null, 0);
        response.addCookie(accessTokenCookie);
        return "redirect:/";
    }
}

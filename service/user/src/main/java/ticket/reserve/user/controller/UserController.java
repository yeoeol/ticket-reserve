package ticket.reserve.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.user.dto.UserLoginRequestDto;
import ticket.reserve.user.dto.UserRegisterRequestDto;
import ticket.reserve.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/users/register")
    public String register(UserRegisterRequestDto requestDto) {
        userService.register(requestDto);
        return "redirect:/users/register";
    }

    @PostMapping("/users/login")
    public String login(UserLoginRequestDto requestDto, HttpServletResponse response) {
        String token = userService.login(requestDto.username(), requestDto.password());

        response.addHeader(HttpHeaders.AUTHORIZATION, token);
        return "redirect:/";
    }

    @GetMapping("/users/{userId}")
    public String get(@PathVariable Long userId) {
        System.out.println("[UserController.get]");
        System.out.println(SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication());
        return "redirect:/events";
    }
}

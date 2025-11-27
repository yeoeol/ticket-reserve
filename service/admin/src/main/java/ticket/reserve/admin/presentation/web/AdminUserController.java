package ticket.reserve.admin.presentation.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;
import ticket.reserve.admin.application.AdminService;
import ticket.reserve.admin.global.util.CookieUtil;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminService adminService;

    @PostMapping("/logout")
    public String logout(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {
        adminService.logout(accessToken);

        Cookie accessTokenCookie = CookieUtil.setHttpOnlyCookie("accessToken", null, 0);
        response.addCookie(accessTokenCookie);
        return "redirect:/admin/home";
    }

    @GetMapping
    public String getUsers(Model model) {
        List<UserResponseDto> users = adminService.getUsers();

        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long userId, Model model) {
        UserResponseDto user = adminService.getUser(userId);

        model.addAttribute("user", user);
        return "admin/userdetails";
    }

    @PostMapping
    public String updateUser(@ModelAttribute UserUpdateRequestDto request) {
        adminService.updateUser(request);
        return "redirect:/admin/users";
    }
}

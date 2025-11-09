package ticket.reserve.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ticket.reserve.admin.client.dto.UserResponseDto;
import ticket.reserve.admin.client.dto.UserUpdateRequestDto;
import ticket.reserve.admin.service.AdminService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        List<UserResponseDto> users = adminService.getUsers();

        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/admin/users/{id}")
    public String getUser(@PathVariable("id") Long userId, Model model) {
        UserResponseDto user = adminService.getUser(userId);

        model.addAttribute("user", user);
        return "admin/userdetails";
    }

    @PostMapping("/admin/users")
    public String modifyUser(@ModelAttribute UserUpdateRequestDto request) {
        adminService.updateUser(request);
        return "redirect:/admin/users";
    }
}

package ticket.reserve.admin.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;
import ticket.reserve.admin.application.port.out.UserPort;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient extends UserPort {

    @GetMapping("/api/users")
    List<UserResponseDto> getUsers();

    @GetMapping("/api/users/{id}")
    UserResponseDto getUser(@PathVariable("id") Long userId);

    @PostMapping("/api/users")
    UserResponseDto updateUser(@RequestBody UserUpdateRequestDto request);
}

package ticket.reserve.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.admin.client.dto.UserResponseDto;
import ticket.reserve.admin.client.dto.UserUpdateRequestDto;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users")
    List<UserResponseDto> getUsers();

    @GetMapping("/api/users/{id}")
    UserResponseDto getUser(@PathVariable("id") Long userId);

    @PostMapping("/api/users")
    UserResponseDto updateUser(@RequestBody UserUpdateRequestDto request);
}

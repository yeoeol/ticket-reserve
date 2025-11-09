package ticket.reserve.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ticket.reserve.admin.client.dto.UserResponseDto;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users")
    List<UserResponseDto> getUsers();

    @GetMapping("/api/users/{id}")
    UserResponseDto getUser(@PathVariable("id") Long userId);
}

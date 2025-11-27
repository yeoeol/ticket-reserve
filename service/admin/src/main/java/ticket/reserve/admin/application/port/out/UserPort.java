package ticket.reserve.admin.application.port.out;

import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;

import java.util.List;
import java.util.Map;

public interface UserPort {
    List<UserResponseDto> getUsers();

    UserResponseDto getUser(Long userId);

    UserResponseDto updateUser(UserUpdateRequestDto request);

    void logout(@RequestBody Map<String, String> request);
}

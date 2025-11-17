package ticket.reserve.admin.application.port.out;

import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;

import java.util.List;

public interface UserPort {
    List<UserResponseDto> getUsers();

    UserResponseDto getUser(Long userId);

    UserResponseDto updateUser(UserUpdateRequestDto request);
}

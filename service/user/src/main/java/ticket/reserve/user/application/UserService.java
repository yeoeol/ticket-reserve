package ticket.reserve.user.application;

import ticket.reserve.user.application.dto.request.UserRegisterRequestDto;
import ticket.reserve.user.application.dto.request.UserUpdateRequestDto;
import ticket.reserve.user.application.dto.response.UserLoginResponseDto;
import ticket.reserve.user.application.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    Long register(UserRegisterRequestDto requestDto);
    UserLoginResponseDto login(String username, String password);
    void logout(String accessToken);

    List<UserResponseDto> findAll();

    UserResponseDto getUser(Long userId);

    UserResponseDto updateUser(UserUpdateRequestDto request);

    void updateLocation(Long userId, Double latitude, Double longitude);
}

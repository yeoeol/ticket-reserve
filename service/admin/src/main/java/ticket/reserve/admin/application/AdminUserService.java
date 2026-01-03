package ticket.reserve.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;
import ticket.reserve.admin.application.port.out.UserPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserPort userPort;

    public void logout(String accessToken) {
        Map<String, String> request = new HashMap<>();
        request.put("accessToken", accessToken);
        userPort.logout(request);
    }

    public List<UserResponseDto> getUsers() {
        return userPort.getUsers();
    }

    public UserResponseDto getUser(Long userId) {
        return userPort.getUser(userId);
    }

    public void updateUser(UserUpdateRequestDto request) {
        userPort.updateUser(request);
    }
}

package ticket.reserve.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.client.user.UserServiceClient;
import ticket.reserve.admin.client.user.dto.UserResponseDto;
import ticket.reserve.admin.client.user.dto.UserUpdateRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserServiceClient userServiceClient;

    public List<UserResponseDto> getUsers() {
        return userServiceClient.getUsers();
    }

    public UserResponseDto getUser(Long userId) {
        return userServiceClient.getUser(userId);
    }

    public void updateUser(UserUpdateRequestDto request) {
        userServiceClient.updateUser(request);
    }


}

package ticket.reserve.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.client.UserServiceClient;
import ticket.reserve.admin.client.dto.UserResponseDto;

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
}

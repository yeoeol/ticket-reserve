package ticket.reserve.admin.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.admin.application.dto.user.request.UserUpdateRequestDto;
import ticket.reserve.admin.application.dto.user.response.UserResponseDto;
import ticket.reserve.admin.application.port.out.UserPort;

import java.util.List;
import java.util.Map;

@Component
public class UserRestClientAdapter implements UserPort {

    private final RestClient restClient;

    public UserRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${endpoints.ticket-reserve-user-service.url}") String userServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();
    }


    @Override
    public List<UserResponseDto> getUsers() {
        return restClient.get()
                .uri("/api/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        return restClient.get()
                .uri("/api/users/{userId}", userId)
                .retrieve()
                .body(UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto request) {
        return restClient.post()
                .uri("/api/users")
                .body(request)
                .retrieve()
                .body(UserResponseDto.class);
    }

    @Override
    public void logout(Map<String, String> request) {
        restClient.post()
                .uri("/api/users/logout")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}

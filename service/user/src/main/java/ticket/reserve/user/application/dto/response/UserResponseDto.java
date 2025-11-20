package ticket.reserve.user.application.dto.response;

import lombok.Builder;
import ticket.reserve.user.domain.user.User;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role
) {

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

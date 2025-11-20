package ticket.reserve.user.application.dto.response;

import lombok.Builder;
import ticket.reserve.user.domain.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email,
        Set<String> roles
) {

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .collect(Collectors.toSet())
                )
                .build();
    }
}

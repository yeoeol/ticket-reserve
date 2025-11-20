package ticket.reserve.admin.application.dto.user.response;

import java.util.List;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        List<String> roles
) {
}

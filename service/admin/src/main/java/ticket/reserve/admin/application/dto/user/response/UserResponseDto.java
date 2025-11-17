package ticket.reserve.admin.application.dto.user.response;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role
) {
}

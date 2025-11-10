package ticket.reserve.admin.client.user.dto;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role
) {
}

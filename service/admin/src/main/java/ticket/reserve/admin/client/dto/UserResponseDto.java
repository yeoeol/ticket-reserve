package ticket.reserve.admin.client.dto;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role
) {
}
